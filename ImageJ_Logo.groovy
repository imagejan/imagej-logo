#@OUTPUT img
import net.imglib2.roi.Masks
import net.imglib2.roi.geom.GeomMasks
//import bdv.util.Bdv
//import bdv.util.BdvFunctions
import net.imglib2.FinalInterval
import net.imglib2.view.Views
import net.imglib2.FinalInterval

import net.imglib2.realtransform.AffineTransform2D


tubeThickness = 12
letterDistance = 5
jRadius = 21
outputSize = 256
frameWeight = 0.1

// required for rasterizing
// to avoid misalignment between J stem and bottom
offset = -0.5

// derived and fixed values
xI = 50 - jRadius
yI = 15
hI = 40
bottomJ = 90

xJbar = xI + tubeThickness + letterDistance
yJbar = 30
xJstem = 50 + jRadius - tubeThickness
yJcenter = bottomJ - jRadius


I = GeomMasks.closedWritableBox([xI, yI] as double[], [xI + tubeThickness, yI + hI] as double[])

Jbar      = GeomMasks.closedWritableBox([xJbar, yJbar] as double[], [50 + jRadius, yJbar + tubeThickness] as double[])
Jstem     = GeomMasks.closedWritableBox([xJstem, yJbar] as double[], [50 + jRadius, yJcenter] as double[])
Jouter    = GeomMasks.openWritableSphere([50, yJcenter] as double[], jRadius)
Jinner    = GeomMasks.closedWritableSphere([50, yJcenter] as double[], jRadius - tubeThickness)
Jclipping = GeomMasks.openWritableBox([ 0, yJcenter] as double[], [100, 100] as double[])

J = Jouter.minus(Jinner).and(Jclipping).or(Jbar).or(Jstem)

slide  = GeomMasks.closedWritableBox([xI - letterDistance, yJcenter - 4] as double[], [xJbar, yJcenter - 3] as double[])

/* Create frame */

frame1 = GeomMasks.closedWritableBox([ 0,  0] as double[], [100, 100] as double[])
frame2 = GeomMasks.closedWritableBox([ frameWeight,  frameWeight] as double[], [100 - frameWeight, 100 - frameWeight] as double[])
frame  = frame1.minus(frame2)

/* Combine mask */

logoRoi = I.or(J).or(slide).or(frame)

/* Translate and scale ROI */

transform = new AffineTransform2D()
transform.scale(outputSize / 100)
transform.translate(offset, offset)

transformedRoi = logoRoi.transform(transform.inverse()) // why inverse?

mask = Masks.toRealRandomAccessibleRealInterval(transformedRoi)

/* Show combined mask in BigDataViewer */

/*
BdvFunctions.show(
				mask,
				new FinalInterval(
						[mask.realMin( 0 ), mask.realMin( 1 ) ] as long[],
						[mask.realMax( 0 ), mask.realMax( 1 ) ] as long[] ),
				"2D Mask",
				Bdv.options() )
*/

/* Use Imglib2 Views to raster the mask */

img = Views.interval(Views.raster(mask), new FinalInterval(
						[mask.realMin( 0 ), mask.realMin( 1 ) ] as long[],
						[mask.realMax( 0 ), mask.realMax( 1 ) ] as long[] )
					)
