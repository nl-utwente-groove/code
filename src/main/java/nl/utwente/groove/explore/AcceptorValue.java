package nl.utwente.groove.explore;

import nl.utwente.groove.explore.encode.EncodedEnabledRule;
import nl.utwente.groove.explore.encode.EncodedPolarity;
import nl.utwente.groove.explore.encode.EncodedRuleFormula;
import nl.utwente.groove.explore.encode.EncodedType;
import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.explore.encode.Template;
import nl.utwente.groove.explore.encode.Template.Template0;
import nl.utwente.groove.explore.encode.Template.Template1;
import nl.utwente.groove.explore.encode.Template.Template2;
import nl.utwente.groove.explore.prettyparse.PAll;
import nl.utwente.groove.explore.prettyparse.PIdentifier;
import nl.utwente.groove.explore.prettyparse.POptional;
import nl.utwente.groove.explore.prettyparse.PSequence;
import nl.utwente.groove.explore.prettyparse.SerializedParser;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.explore.result.AnyStateAcceptor;
import nl.utwente.groove.explore.result.CycleAcceptor;
import nl.utwente.groove.explore.result.FinalStateAcceptor;
import nl.utwente.groove.explore.result.NoStateAcceptor;
import nl.utwente.groove.explore.result.Predicate;
import nl.utwente.groove.explore.result.PredicateAcceptor;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.lts.GraphState;

/** Symbolic values for the implemented acceptors. */
public enum AcceptorValue implements ParsableValue {
    /** Acceptor for final states. */
    FINAL("final", "Final States",
        "This acceptor succeeds when a state is added to the LTS that is "
            + "<I>final</I>. A state is final when no modifying rule is" + "applicable on it."),
    /** Acceptor for states where a given invariant rule is applicable. */
    INVARIANT("inv", "Check Invariant",
        "This acceptor succeeds when a state is reached in which the "
            + "indicated rule is applicable. Note that this is detected "
            + "<I>before</I> the rule has been applied.<BR> "
            + "This acceptor ignores rule priorities."),
    /** Acceptor for states reached by the application of a certain rule. */
    RULE("ruleapp", "Rule Application",
        "This acceptor succeeds when a transition of the indicated rule is "
            + "added to the LTS. Note that this is detected <I>after</I> "
            + "the rule has been applied (which means that rule scheduling "
            + "is taken into account)."),
    /** Acceptor for states that satisfy a given rule formula. */
    FORMULA("formula", "Rule Formula",
        "This acceptor is a variant of Check Invariant that succeeds when a"
            + " state is reached in which an arbitrary rule <i>formula</i> " + "is applicable."),
    /** Acceptor for arbitrary states. */
    ANY("any", "Any State",
        "This acceptor succeeds whenever an (exposed) state is added to the LTS."),
    /** Acceptor for cycles. */
    CYCLE("cycle", "Cycles",
        "This acceptor listens to pairs of graph states and Buchi states,"
            + "and succeeds when a pair is added that lies on a cycle with an"
            + "accepting Buchi state. Should only be used in conjunction with "
            + "LTL model checking."),
    /** Acceptor that does not accept any states. */
    NONE("none", "No State", "This acceptor always fails whenever a state is added to the LTS.");

    private AcceptorValue(String keyword, String name, String description) {
        this.keyword = keyword;
        this.name = name;
        this.description = description;
    }

    /** Returns the identifying keyword of this acceptor value. */
    @Override
    public String getKeyword() {
        return this.keyword;
    }

    /** Returns the name of this acceptor value. */
    @Override
    public String getName() {
        return this.name;
    }

    /** Returns the description of this acceptor value. */
    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Serialized toSerialized() {
        return new Serialized(getKeyword());
    }

    @Override
    public boolean isDevelopment() {
        return false;
    }

    @Override
    public boolean isDefault(GrammarModel grammar) {
        ExploreType exploration = grammar.getDefaultExploreType();
        return exploration.getAcceptor().getKeyword().equals(getKeyword());
    }

    /** Creates the appropriate template for this acceptor. */
    public Template<Acceptor> getTemplate() {
        switch (this) {
        case ANY:
            return new MyTemplate0() {
                @Override
                public Acceptor create() {
                    return AnyStateAcceptor.PROTOTYPE;
                }
            };

        case CYCLE:
            return new MyTemplate0() {
                @Override
                public Acceptor create() {
                    return CycleAcceptor.PROTOTYPE;
                }
            };

        case FINAL:
            return new MyTemplate0() {
                @Override
                public Acceptor create() {
                    return FinalStateAcceptor.PROTOTYPE;
                }
            };

        case FORMULA:
            return new MyTemplate1<>(new PAll("formula"), "formula", new EncodedRuleFormula()) {
                @Override
                public Acceptor create(Predicate<GraphState> predicate) {
                    return new PredicateAcceptor(predicate);
                }
            };

        case INVARIANT:
            PSequence parser = new PSequence(
                new POptional("!", "polarity", EncodedPolarity.NEGATIVE, EncodedPolarity.POSITIVE),
                new PIdentifier("rule"));
            return new MyTemplate2<>(parser, "rule", new EncodedEnabledRule(), "polarity",
                new EncodedPolarity()) {

                @Override
                public Acceptor create(Rule rule, Boolean polarity) {
                    Predicate<GraphState> P = new Predicate.RuleApplicable(rule);
                    if (!polarity) {
                        P = new Predicate.Not<>(P);
                    }
                    return new PredicateAcceptor(P);
                }
            };

        case NONE:
            return new MyTemplate0() {
                @Override
                public Acceptor create() {
                    return NoStateAcceptor.INSTANCE;
                }
            };

        case RULE:
            return new MyTemplate1<>(new PIdentifier("rule"), "rule", new EncodedEnabledRule()) {

                @Override
                public Acceptor create(Rule rule) {
                    return new PredicateAcceptor(new Predicate.ActionApplied(rule));
                }
            };

        default:
            // can't get here
            throw new IllegalStateException();
        }
    }

    private final String keyword;
    private final String name;
    private final String description;

    /** Specialised parameterless template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate0 extends Template0<Acceptor> {
        public MyTemplate0() {
            super(AcceptorValue.this);
        }
    }

    /** Specialised 1-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate1<T1> extends Template1<Acceptor,T1> {
        public MyTemplate1(SerializedParser parser, String name, EncodedType<T1,String> type) {
            super(AcceptorValue.this, parser, name, type);
        }
    }

    /** Specialised 2-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate2<T1,T2> extends Template2<Acceptor,T1,T2> {
        public MyTemplate2(SerializedParser parser, String name1, EncodedType<T1,String> type1,
                           String name2, EncodedType<T2,String> type2) {
            super(AcceptorValue.this, parser, name1, type1, name2, type2);
        }
    }
}