//The following code can be used in a junit test to perform confluence analysis on one of these grammars

    @Test
    public void testGrammar() {
	//path to grammar
        String grammarStr = "path/to/grammar.gps/";
        File grammarFile = new File(grammarStr);
        GrammarModel view = null;
        Grammar grammar = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
            grammar = view.toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

	//set to true to use a different method for confluence analysis which may be slightly more efficient
	boolean alternatemethod = false;
	
	//expected results depends on the grammar
        ConfluenceStatus expected = ConfluenceStatus.NOT_STICTLY_CONFLUENT;
        ConfluenceResult result =
            ConfluenceResult.checkStrictlyConfluent(grammar,
                ConfluenceStatus.UNTESTED, alternatemethod);

	//total amount of critical pairs
        int totalPairs = result.getSizeOfUntestedPairs();
        result.analyzeAll();
	//result has methods to get the non-strictly confluent pairs

        assertTrue(result.getStatus() == expected);

    }