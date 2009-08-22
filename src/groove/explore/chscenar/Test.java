package groove.explore.chscenar;

import groove.explore.chscenar.parser.ConfigLexer;
import groove.explore.chscenar.parser.ConfigParser;
import groove.util.Groove;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

public class Test {

    public static void main(String args[]) {

        test3();

    }

    static void test3() {
        ScenarioChecker sc = new ScenarioChecker();
        boolean allowed =
            sc.isAllowed(groove.explore.strategy.LinearStrategy.class,
                groove.explore.result.Result.class,
                groove.explore.result.FinalStateAcceptor.class);
        System.out.println(allowed);
    }

    static void test2() {
        ConfigParser parser = null;
        try {
            // ConfigLexer lexer = new ConfigLexer(new
            // ANTLRFileStream("/local/bonevai/Workspace/GrooveNoAbstr/src/groove/util/explore/parser/configuration"));
            URL configURL =
                Groove.getResource(Groove.ALLOWED_SCENARIOS_CONFIGURATION_FILE);
            InputStream in = configURL.openStream();
            ConfigLexer lexer = new ConfigLexer(new ANTLRInputStream(in));
            in.close();
            parser = new ConfigParser(new CommonTokenStream(lexer));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ConfigParser.prog_return rule = null;
        try {
            rule = parser.prog();
        } catch (RecognitionException e) {
            e.printStackTrace();
            System.err.println("Aborting");
            System.exit(1);
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                System.out.println("Error : " + e.getMessage() + ". Aborting");
                System.exit(1);
            }
        }

        System.out.println(rule);

    }

    static void test1() {

        AllowRuleImpl rule = new AllowRuleImpl();
        SRASetImpl allow = new SRASetImpl();
        SRASetImpl forbid = new SRASetImpl();
        try {
            allow.addStrategy(Class.forName("groove.explore.strategy.Strategy"));
            allow.addResult(Class.forName("groove.explore.result.SizedResult"));
            allow.addAcceptor(Class.forName("groove.explore.result.Acceptor"));
        } catch (ClassNotFoundException e) {
            // should never happen
            e.printStackTrace();
        }

        rule.setAllowed(allow);

        try {
            forbid.addStrategy(Class.forName("groove.explore.strategy.ConditionalStrategy"));
            forbid.addStrategy(Class.forName("groove.explore.strategy.ConditionalDepthFirstStrategy"));
            forbid.addResult(Class.forName("groove.explore.result.SizedResult"));
            forbid.addAcceptor(Class.forName("groove.explore.result.EmptyAcceptor"));
        } catch (ClassNotFoundException e) {
            // should never happen
            e.printStackTrace();
        }

        rule.addForbidden(forbid);

        System.out.println(rule);

        Class<?> str = null, res = null, acc = null;

        try {
            str =
                Class.forName("groove.explore.strategy.ConditionalBreadthFirstStrategy");
            res = Class.forName("groove.explore.result.SizedResult");
            acc = Class.forName("groove.explore.result.EmptyAcceptor");
        } catch (ClassNotFoundException e) {
            // should never happen
            e.printStackTrace();
        }

        System.out.println("false : "
            + rule.isAllowedConfiguration(str, res, acc));

        try {
            str = Class.forName("groove.explore.strategy.BreadthFirstStrategy");
            res = Class.forName("groove.explore.result.SizedResult");
            acc = Class.forName("groove.explore.result.EmptyAcceptor");
        } catch (ClassNotFoundException e) {
            // should never happen
            e.printStackTrace();
        }

        System.out.println("true : "
            + rule.isAllowedConfiguration(str, res, acc));
    }

    enum Component {
        STR, RES, ACC;
    }

    Class<?> getClass(String name, Component c) {
        String n = null;
        if (name.contains(".")) {
            n = name;
        } else {
            switch (c) {
            case STR:
                n = "groove.explore.strategy." + name;
                break;
            case RES:
            case ACC:
                n = "groove.explore.result." + name;
                break;
            }
        }
        try {
            return Class.forName(n);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null; // todo : throw an exception here
        }
    }

}
