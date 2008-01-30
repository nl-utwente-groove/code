package groove.control;

public class StateSetTest {	public static void main(String[] args) {		ControlState a = new ControlState(null);		ControlState b = new ControlState(null);		ControlState c = new ControlState(null);		ControlState d = new ControlState(null);		StateSet s1 = new StateSet();
		StateSet s2 = new StateSet();		StateSet s3 = new StateSet();		StateSet s4 = new StateSet();
		s1.add(a);		s1.add(b);		s2.add(a);		s2.add(b);		s3.add(a);		s3.add(b);		s4.add(a);		s4.add(b);		s4.add(c);
		System.out.println("s1 equals s2:" + printEquals(s1,s2));		System.out.println("s2 equals s1:" + printEquals(s2,s1));		System.out.println("s3 equals s4:" + printEquals(s3,s4));		System.out.println("s4 equals s3:" + printEquals(s4,s3));	}
	
	public static String printEquals(Object o1, Object o2) {
		return (o1.equals(o2)?"true":"false");
	}
	
}
