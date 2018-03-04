#@OUTPUT img
import net.imglib2.roi.Masks
import net.imglib2.roi.geom.GeomMasks
import bdv.util.Bdv
import bdv.util.BdvFunctions
import net.imglib2.FinalInterval
import net.imglib2.view.Views
import net.imglib2.FinalInterval


I = GeomMasks.closedWritableBox([30.5, 10.5] as double[], [40.5, 60.5] as double[])

Jbar      = GeomMasks.closedWritableBox([45.5, 30.5] as double[], [70.5, 40.5] as double[])
Jstem     = GeomMasks.closedWritableBox([60.5, 30.5] as double[], [70.5, 70.5] as double[])
Jouter    = GeomMasks.openWritableSphere([50.5, 70.5] as double[], 20.0)
Jinner    = GeomMasks.closedWritableSphere([50.5, 70.5] as double[], 10.0)
Jclipping = GeomMasks.openWritableBox([ 0.5, 70.5] as double[], [100.5, 100.5] as double[])

J = Jouter.minus(Jinner).and(Jclipping).or(Jbar).or(Jstem)

slide  = GeomMasks.closedWritableBox([25.5, 66.5] as double[], [45.5, 68.5] as double[])

/* Create frame */

frame1 = GeomMasks.closedWritableBox([ 0.5,  0.5] as double[], [100.5, 100.5] as double[])
frame2 = GeomMasks.closedWritableBox([ 1.5,  1.5] as double[], [99.5, 99.5] as double[])
frame  = frame1.minus(frame2)

/* Combine mask */

mask = Masks.toRealRandomAccessibleRealInterval(I.or(J).or(slide).or(frame))

/* Show combined mask in BigDataViewer */

BdvFunctions.show(
				mask,
				new FinalInterval(
						[mask.realMin( 0 ), mask.realMin( 1 ) ] as long[],
						[mask.realMax( 0 ), mask.realMax( 1 ) ] as long[] ),
				"2D Mask",
				Bdv.options() )

/* Use Imglib2 Views to raster the mask */

img = Views.interval(Views.raster(mask), new FinalInterval(
						[mask.realMin( 0 ), mask.realMin( 1 ) ] as long[],
						[mask.realMax( 0 ), mask.realMax( 1 ) ] as long[] )
					)
