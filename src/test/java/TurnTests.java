import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurnTests {

	@Test
	void isIntersectionEmpty(){
		Intersection intersection = new Intersection(1, 1, PlayerColor.NONE);
		assertEquals(intersection.getStone(), PlayerColor.NONE);
	}

}
