import com.holub.life.ZoomModel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ZoomModelTest {

    @Test
    void startsAtOneHundredPercent() {
        ZoomModel zoom = new ZoomModel();

        assertEquals(1.0, zoom.getZoomLevel());
        assertEquals(100, zoom.getZoomPercent());
    }

    @Test
    void zoomInThenOutReturnsToStart() {
        ZoomModel zoom = new ZoomModel();

        zoom.zoomIn();
        assertEquals(125, zoom.getZoomPercent());

        zoom.zoomOut();
        assertEquals(1.0, zoom.getZoomLevel(), 0.0001);
    }

    @Test
    void zoomIsLimited() {
        ZoomModel zoom = new ZoomModel();

        zoom.setZoomLevel(100);
        assertEquals(3.0, zoom.getZoomLevel());

        zoom.setZoomLevel(0.001);
        assertEquals(0.25, zoom.getZoomLevel());
    }

    @Test
    void rejectsInvalidZoom() {
        ZoomModel zoom = new ZoomModel();

        assertThrows(IllegalArgumentException.class,
                () -> zoom.setZoomLevel(Double.NaN));
    }

    @Test
    void listenersHearEveryChange() {
        // the zoom label depends on this (it used to miss ctrl+wheel zoom)
        ZoomModel zoom = new ZoomModel();
        List<Double> heard = new ArrayList<>();
        zoom.addZoomListener(heard::add);

        zoom.zoomIn();
        zoom.setZoomLevel(2.0);
        zoom.resetZoom();
        zoom.resetZoom(); // no change, no event

        assertEquals(3, heard.size());
        assertEquals(1.0, heard.get(2));
    }

    @Test
    void cellSizeIsAlwaysWholePixels() {
        ZoomModel zoom = new ZoomModel();

        assertEquals(8, zoom.getCellPixels(8));

        zoom.setZoomLevel(1.77);
        assertEquals(14, zoom.getCellPixels(8));

        zoom.setZoomLevel(0.25);
        assertEquals(2, zoom.getCellPixels(8));
        assertEquals(1, zoom.getCellPixels(1), "never smaller than one pixel");
    }
}
