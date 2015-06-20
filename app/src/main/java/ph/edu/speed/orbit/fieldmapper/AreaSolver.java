package ph.edu.speed.orbit.fieldmapper;

/**
 * Created by jaredsalazar on 6/20/15.
 */
public class AreaSolver {

    static double Area;
    double pArea;

    public AreaSolver(Double[] lat, Double[] lng, int count) {
        pArea = 0.0;
        for(int i = 0; i < count; i++ ){
            pArea = pArea + (lat[i]-lat[i+1])*(lng[i]+lng[i+1]);
        }
        Area = Math.abs(pArea)/2;
    }
}
