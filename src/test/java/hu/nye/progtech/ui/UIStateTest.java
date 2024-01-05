package hu.nye.progtech.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UIStateTest {

    UIState state;
    @Test
    void print() {
        state = new UIState("test",c-> Arrays.asList("HEADER","SECONDH").stream().toList(),c->new ArrayList<>());

        List<String> collector = new ArrayList<>();
        state.print(collector::add,null);

        for ( int i=0 ; i < UIState.HEADER_GAP_SIZE; i++){
            assertEquals("",collector.get(i));
        }

        assertEquals("HEADER",collector.get(UIState.HEADER_GAP_SIZE));
        assertEquals("SECONDH",collector.get(UIState.HEADER_GAP_SIZE+1));

    }

    @Test
    void hasAction() {
        state = new UIState("test",null, UIMenuAction.create("1",c->null),
                UIMenuAction.create("2",c->null),
                UIMenuAction.create("3",c->null,c->false)
        );

        assertTrue(state.hasAction(0, null));
        assertTrue(state.hasAction(1, null));
        assertFalse(state.hasAction(2, null)); //disabled
        assertFalse(state.hasAction(3, null));
    }

}