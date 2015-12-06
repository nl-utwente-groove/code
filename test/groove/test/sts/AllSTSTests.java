package groove.test.sts;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Vincent de Bruijn
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({STSTest.class, GateTest.class, VariableTest.class,
    LocationTest.class, SwitchRelationTest.class})
public class AllSTSTests {
    // Empty by design
}
