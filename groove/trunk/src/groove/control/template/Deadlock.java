/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.control.template;

import groove.control.CtrlVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public final class Deadlock implements TemplatePosition {
    /**
     * Constructor a canonical instance of deadlock at a certain transit depth.
     */
    private Deadlock(int depth) {
        this.depth = depth;
    }

    public Type getType() {
        return Type.DEAD;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isDead() {
        return true;
    }

    @Override
    public boolean isTrial() {
        return false;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    private final int depth;

    @Override
    public MultiSwitch getAttempt() {
        return null;
    }

    @Override
    public TemplatePosition onFailure() {
        return null;
    }

    @Override
    public TemplatePosition onSuccess() {
        return null;
    }

    @Override
    public Stage getFirstStage() {
        if (this.slot == null) {
            this.slot = new Stage(this, 0, false);
        }
        return this.slot;
    }

    private Stage slot;

    public Map<CtrlVar,Integer> getVarIxMap() {
        return Collections.emptyMap();
    }

    /* We use negative numbers to distinguish the deadlock positions from locations. */
    public int getNumber() {
        return -1 - getDepth();
    }

    public int compareTo(TemplatePosition o) {
        return getNumber() - o.getNumber();
    }

    /** Returns the canonical instance at a certain transit depth. */
    public static Deadlock instance(int depth) {
        for (int i = instances.size(); i <= depth; i++) {
            instances.add(new Deadlock(i));
        }
        return instances.get(depth);
    }

    private static final List<Deadlock> instances = new ArrayList<Deadlock>();
}
