import com.example.application.helper.CoordinateHelper;
import com.vaadin.flow.component.map.configuration.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleCoordinateHelperTest {

    @Test
    public void measureDistancebBetweenTwoCoordinates(){
        Coordinate rostock = new Coordinate(54.083336, 12.108811);
        Coordinate muenchen = new Coordinate(48.137154, 11.576124);
        double distanceBetweenTwoPoints = CoordinateHelper.measureDistanceBetweenTwoPoints(rostock, muenchen);
        System.out.println(distanceBetweenTwoPoints);
    }
}
