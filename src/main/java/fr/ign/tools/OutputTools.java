package fr.ign.tools;

import java.io.File;
import java.io.IOException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;

import fr.ign.cogit.GTFunctions.Vectors;

public class OutputTools {
	public static SimpleFeatureSource VectorizeMupOutput(GridCoverage2D coverage, File destFile, double sizeCell) throws IOException, NoSuchAuthorityCodeException, FactoryException, ParseException {

		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:2154");
		ReferencedEnvelope gridBounds = new ReferencedEnvelope(coverage.getEnvelope2D().getMinX(), coverage.getEnvelope2D().getMaxX(), coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY(), sourceCRS);

		SimpleFeatureTypeBuilder sfTypeBuilder = new SimpleFeatureTypeBuilder();
		sfTypeBuilder.setName("testType");
		sfTypeBuilder.setCRS(sourceCRS);
		sfTypeBuilder.add("the_geom", Polygon.class);
		sfTypeBuilder.setDefaultGeometry("the_geom");

		sfTypeBuilder.add("eval", Float.class);

		SimpleFeatureType featureType = sfTypeBuilder.buildFeatureType();
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(featureType);

		DefaultFeatureCollection output = new DefaultFeatureCollection();

		SimpleFeatureSource grid = Grids.createSquareGrid(gridBounds, sizeCell);

		int i = 0;
		SimpleFeatureIterator featIt = grid.getFeatures().features();
		try {
			while (featIt.hasNext()) {
				SimpleFeature feat = featIt.next();

				DirectPosition2D coord = new DirectPosition2D((feat.getBounds().getMaxX() - feat.getBounds().getHeight() / 2),
						(feat.getBounds().getMaxY() - feat.getBounds().getHeight() / 2));
				float[] yo = (float[]) coverage.evaluate(coord);
				if (yo[0] > 0) {
					i = i + 1;
					Object[] attr = { yo[0] };
					sfBuilder.add(feat.getDefaultGeometry());
					SimpleFeature feature = sfBuilder.buildFeature("id" + i, attr);
					output.add(feature);
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			featIt.close();
		}
		Vectors.exportSFC(output.collection(), destFile);
		return grid;
	}
}
