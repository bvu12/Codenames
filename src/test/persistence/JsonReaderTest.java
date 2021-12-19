package persistence;

import org.junit.jupiter.api.Test;

public class JsonReaderTest extends JsonWriterTest{

    @Test
    void testReaderEmptyGamestate() {
        super.testWriterEmptyGamestate();
    }

    @Test
    void testReaderGeneralGamestate() {
        super.testWriterGeneralGamestate();
    }
}
