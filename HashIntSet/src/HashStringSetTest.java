import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class HashStringSetTest {
    @Test
    public void test() throws Exception {
        HashStringSet st = new HashStringSet("freddy", "fazbear", "fazbear");
        Assertions.assertEquals(st.toString(), "[fazbear, freddy]");

    }

    @Test
    public void testRemove() {
        HashStringSet st = new HashStringSet("freddy", "fazbear", "fazbear");
        st.remove("fazbear");
        Assertions.assertEquals(st.toString(), "[freddy]");
    }

    @Test
    public void testRehash(){
        int numToAdd = 10000;
        int buckets = 10;
        double maxLoad = HashStringSet.MAX_LOAD;

        HashStringSet hash = new HashStringSet();
        for(int i = 1; i <= numToAdd; i++){
            hash.add("" + i);
            double expectedLoad = (double) i / buckets;
            if(expectedLoad > maxLoad) {
            buckets *= 2;
            expectedLoad = (double) i / buckets;
            }
            assertEquals(expectedLoad,hash.loadFactor(), 0.000001);
        }
        for(int i = 1; i <= numToAdd; i++){
            assertTrue(hash.contains(""+i));
        }
        assertFalse(hash.contains("" + (numToAdd+1)));
        
    }



}