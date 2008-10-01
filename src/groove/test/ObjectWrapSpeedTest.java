/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: ObjectWrapSpeedTest.java,v 1.2 2008-01-30 09:33:07 iovka Exp $
 */
package groove.test;

import groove.util.Reporter;

import java.util.Collection;
import java.util.Collections;

/**
 * Class to test the overhead of wrapping an object up.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ObjectWrapSpeedTest {
    /** Constructs a version of the test. */
    public ObjectWrapSpeedTest(String version, Action storeAction, Action getAction) {
        this.version = version;
        measure = reporter.newMethod(version);
        this.storeAction = storeAction;
        this.getAction = getAction;
    }
    
    /** Starts the test. */
    public void start() {
        test();
        System.out.printf("Results for %s: %s ms%n", version, reporter.getTotalTime(measure));
    }
    
    private void test() {
        reporter.start(measure);
        for (int i = 0; i < BOUND; i++) {
            getAction.start(storeAction.start(new Object()));
        }
        reporter.stop();
    }
    
    private final String version;
    private final int measure;
    private final Action storeAction;
    private final Action getAction;
    /**
     * @param args
     */
    public static void main(String[] args) {
        new ObjectWrapSpeedTest("Bare", new Action() {
            public Object start(Object object) {
                return object;
            }
        }, new Action() {
            public Object start(Object object) {
                return object;
            }            
        }).start();
        new ObjectWrapSpeedTest("Wrapped", new Action() {
            public Object start(Object object) {
                return new Wrapper(object);
            }
        }, new Action() {
            public Object start(Object object) {
                return ((Wrapper) object).get();
            }            
        }).start();
        new ObjectWrapSpeedTest("Set", new Action() {
            public Object start(Object object) {
                return Collections.singleton(object);
            }
        }, new Action() {
            public Object start(Object object) {
                return ((Collection<?>) object).iterator().next();
            }            
        }).start();
        new ObjectWrapSpeedTest("Array", new Action() {
            public Object start(Object object) {
                return new Object[] { object };
            }
        }, new Action() {
            public Object start(Object object) {
                return ((Object[]) object)[0];
            }            
        }).start();
    }
    
	static private final int BOUND = 100000000;	
	
	static private final Reporter reporter = Reporter.register(ObjectWrapSpeedTest.class);
	
	/** Interface for an action to be repeatedly invoked in the test. */
	private static interface Action {
	    /** Starts the action. */
	    Object start(Object object);
	}
	
	/** Wrapper class. */
	private static class Wrapper {
	    /** Creates a wrapper instance for a given object. */
	    Wrapper(Object object) {
	        this.object = object;
	    }
	    
	    /** Retrieves the wrapped object. */
	    final Object get() {
	        return object;
	    }
	    
	    private Object object;
	}
}
