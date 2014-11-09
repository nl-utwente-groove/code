/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package groove.gui;

import groove.gui.display.DisplayKind;
import groove.gui.display.LTSDisplay;
import groove.io.GrooveFileChooser;
import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;
import groove.util.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Class that saves some basic information on the status of the Simulator.
 * @author Eduardo Zambon
 */
public class UserSettings {
    /** Reads and applies previously stored settings. */
    public static void applyUserSettings(Simulator simulator) {
        for (Setting pref : settings) {
            pref.apply(simulator);
        }
    }

    /** Synchronises saved settings with the current ones. */
    public static void syncSettings(Simulator simulator) {
        for (Setting pref : settings) {
            pref.sync(simulator);
        }
    }

    /** The persistently stored user preferences. */
    private static final Preferences userPrefs = Options.userPrefs;
    /** Key for the selected display. */
    private static final String DISPLAY_KEY = "Selected display";
    /** Key for the divider position in the grammar panel. */
    static private final String DISPLAYS_INFO_DIV_POS_KEY = "Displays+info panel divider position";
    /** Key for the divider position in the main panel. */
    static private final String GRAMMAR_DIV_POS_KEY = "Main panel divider position";
    /** Key for the divider position in the lists panel. */
    static private final String LISTS_DIV_POS_KEY = "Rule-Graph divider position";
    /** Key for the state bound in the exploration progress dialog. */
    static private final String STATE_BOUND_KEY = "Maximum state displayed";
    /** Key for the preferences of the known file choosers. */
    static private final String CHOOSER_KEY = "File chooser preferences";

    private static List<Setting> settings = new ArrayList<>();

    static {
        settings.add(new SizeSetting());
        settings.add(new LocationSetting());
        settings.add(new FuncSetting(key(GRAMMAR_DIV_POS_KEY), //
            sim -> "" + sim.getGrammarPanel().getDividerLocation(), //
            (sim, val) -> sim.getGrammarPanel().setDividerLocation(Integer.parseInt(val))));
        settings.add(new FuncSetting(key(DISPLAYS_INFO_DIV_POS_KEY), //
            sim -> "" + sim.getDisplaysInfoPanel().getDividerLocation(),//
            (sim, val) -> sim.getDisplaysInfoPanel().setDividerLocation(Integer.parseInt(val))));
        settings.add(new FuncSetting(key(LISTS_DIV_POS_KEY),//
            sim -> "" + sim.getListsPanel().getDividerLocation(),//
            (sim, val) -> sim.getListsPanel().setDividerLocation(Integer.parseInt(val))));
        settings.add(new FuncSetting(key(DISPLAY_KEY),//
            sim -> sim.getModel().getDisplay().name(),//
            (sim, val) -> sim.getModel().setDisplay(DisplayKind.valueOf(val))));
        settings.add(new FuncSetting(key(STATE_BOUND_KEY, "" + 1000),//
            sim -> "" + getLTSDisplay(sim).getStateBound(),//
            (sim, val) -> getLTSDisplay(sim).setStateBound(Integer.parseInt(val))));
        settings.add(new FuncSetting(key(CHOOSER_KEY),//
            sim -> GrooveFileChooser.getPreferences(),//
            (sim, val) -> GrooveFileChooser.setPreferences(val)));
    }

    private static LTSDisplay getLTSDisplay(Simulator sim) {
        return ((LTSDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.LTS));
    }

    private static interface Setting {
        /** Applies these preferences to a given simulator. */
        void apply(Simulator sim);

        /** Stores the values from the simulator back into the user preferences. */
        void sync(Simulator sim);
    }

    private abstract static class SingleSetting implements Setting {
        SingleSetting(Key key) {
            this.key = key;
        }

        private String getName() {
            return this.key.one();
        }

        private String getDef() {
            return this.key.two();
        }

        private boolean isRemove() {
            return this.key.three();
        }

        private final Key key;

        @Override
        public void apply(Simulator sim) {
            String value = userPrefs.get(getName(), getDef());
            if (value != null && !value.isEmpty()) {
                if (isRemove()) {
                    userPrefs.remove(getName());
                }
                SwingUtilities.invokeLater(() -> {
                    try {
                        setValue(sim, value);
                    } catch (RuntimeException exc) {
                        // Ignore any exceptions
                    }
                });
            }
        }

        @Override
        public void sync(Simulator sim) {
            try {
                String value = getValue(sim);
                if (value == null || value.isEmpty()) {
                    userPrefs.remove(getName());
                } else {
                    userPrefs.put(getName(), value);
                }
            } catch (RuntimeException exc) {
                // Ignore any exceptions
            }
        }

        abstract void setValue(Simulator sim, String value);

        abstract String getValue(Simulator sim);
    }

    private abstract static class MultiSetting implements Setting {
        MultiSetting(String... keys) {
            this.keys = new Key[keys.length];
            for (int i = 0; i < keys.length; i++) {
                this.keys[i] = key(keys[i]);
            }
        }

        private final Key[] keys;

        @Override
        public void apply(Simulator sim) {
            Values values = new Values();
            boolean ok = true;
            // fetch user preference values for all keys
            for (Key key : this.keys) {
                String value = userPrefs.get(key.one(), key.two());
                if (value == null || value.isEmpty()) {
                    // stop it, we don't have all values
                    ok = false;
                    break;
                }
                values.add(value);
            }
            if (ok) {
                // remove keys if so required
                for (int i = 0; i < this.keys.length; i++) {
                    if (this.keys[i].three()) {
                        userPrefs.remove(this.keys[i].one());
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    // apply the values to the simulator
                    try {
                        setValue(sim, values);
                    } catch (RuntimeException exc) {
                        // Ignore any exceptions
                    }
                });
            }
        }

        @Override
        public void sync(Simulator sim) {
            try {
                Values values = getValue(sim);
                for (int i = 0; i < values.size(); i++) {
                    String name = this.keys[i].one();
                    String val = values.get(i);
                    if (val == null || val.isEmpty()) {
                        userPrefs.remove(name);
                    } else {
                        userPrefs.put(name, val);
                    }
                }
            } catch (RuntimeException exc) {
                // Ignore any exceptions
            }
        }

        abstract void setValue(Simulator sim, Values vals);

        abstract Values getValue(Simulator sim);
    }

    private static class LocationSetting extends SingleSetting {
        public LocationSetting() {
            super(key(LOCATION_KEY, "", true));
        }

        @Override
        void setValue(Simulator sim, String value) {
            try {
                final SystemStore store = SystemStoreFactory.newStore(value);
                sim.getActions().getLoadGrammarAction().load(store);
            } catch (IOException e) {
                // don't load if we're going to be difficult
            }
        }

        @Override
        String getValue(Simulator sim) {
            String result = null;
            SimulatorModel model = sim.getModel();
            if (model != null) {
                SystemStore store = model.getStore();
                if (store != null) {
                    result = store.getLocation().toString();
                }
            }
            return result;
        }

        /** Key for the grammar location. */
        private static final String LOCATION_KEY = "Grammar location";
    }

    /** Setting for the size of the simulator window. */
    private static class SizeSetting extends MultiSetting {
        SizeSetting() {
            super(SIM_MAX_KEY, SIM_WIDTH_KEY, SIM_HEIGHT_KEY);
        }

        @Override
        void setValue(Simulator sim, Values vals) {
            JFrame frame = sim.getFrame();
            if (Boolean.parseBoolean(vals.get(0))) {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                int w = Integer.parseInt(vals.get(1));
                int h = Integer.parseInt(vals.get(2));
                frame.setSize(w, h);
            }
        }

        @Override
        Values getValue(Simulator sim) {
            JFrame frame = sim.getFrame();
            String simMax = "" + (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH);
            String simWidth = "" + frame.getWidth();
            String simHeight = "" + frame.getHeight();
            return vals(simMax, simWidth, simHeight);
        }

        static private final String SIM_HEIGHT_KEY = "Simulator height";
        static private final String SIM_MAX_KEY = "Simulator maximized";
        static private final String SIM_WIDTH_KEY = "Simulator width";
    }

    private static class FuncSetting extends SingleSetting {
        FuncSetting(Key key, Function<Simulator,String> getter, BiConsumer<Simulator,String> setter) {
            super(key);
            this.getter = getter;
            this.setter = setter;
        }

        private final Function<Simulator,String> getter;
        private final BiConsumer<Simulator,String> setter;

        @Override
        void setValue(Simulator sim, String value) {
            this.setter.accept(sim, value);
        }

        @Override
        String getValue(Simulator sim) {
            return this.getter.apply(sim);
        }
    }

    static private Key key(String name) {
        return new Key(name, "", false);
    }

    static private Key key(String name, String def) {
        return new Key(name, def, false);
    }

    static private Key key(String name, String def, boolean remove) {
        return new Key(name, def, remove);
    }

    private static class Key extends Triple<String,String,Boolean> {
        public Key(String one, String two, Boolean three) {
            super(one, two, three);
        }
    }

    private static Values vals(String... args) {
        Values result = new Values();
        result.addAll(Arrays.asList(args));
        return result;
    }

    private static class Values extends ArrayList<String> {
        // empty
    }
}
