package mp.utl_odb.tree.tree_examples;

import mpe.core.P;
import mpf.test.ZNViewAno;

@ZNViewAno
public class UTreeAllTest {

	public static void main(String[] args) {
		P.warnBig("Test tree7 simple store");
		UTree5_ExampleSimpleKeyValueStore.main(args);
//		P.exit("!!!!!!!!!!! test only for tree7");
		P.warnBig("Test tree3 simple store");
		UTree_ExampleSimpleKeyValueStore.main(args);
		P.warnBig("Test EveryLifeCache");
		UTree_ExampleEveryLifeCache.main(args);
		P.warnBig("Test ShortLifeCache");
		UTree_ExampleShortLifeCache.main(args);
	}

}
