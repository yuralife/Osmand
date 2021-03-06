package net.osmand.plus.parkingpoint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.util.TimeUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import net.osmand.Location;
import net.osmand.data.LatLon;
import net.osmand.plus.OsmAndFormatter;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.R;
import net.osmand.plus.dashboard.DashLocationFragment;
import net.osmand.plus.helpers.FontCache;
import net.osmand.util.MapUtils;

import java.util.Calendar;

/**
 * Created by Denis on
 * 26.01.2015.
 */
public class DashParkingFragment extends DashLocationFragment {
	public static final String TAG = "DASH_PARKING_FRAGMENT";
	ParkingPositionPlugin plugin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.dash_parking_fragment, container, false);
		Typeface typeface = FontCache.getRobotoMedium(getActivity());
		Button remove = (Button) view.findViewById(R.id.remove_tag);
		remove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog dialog = plugin.showDeleteDialog(getActivity());
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						updateParkingPosition();
					}
				});
			}
		});
		remove.setTypeface(typeface);

		return view;
	}

	@Override
	public void onOpenDash() {
		plugin = OsmandPlugin.getEnabledPlugin(ParkingPositionPlugin.class);
		updateParkingPosition();
	}

	private void updateParkingPosition() {
		View mainView = getView();
		if (mainView == null) {
			return;
		}
		if (plugin == null || plugin.getParkingPosition() == null) {
			mainView.setVisibility(View.GONE);
			return;
		} else {
			mainView.setVisibility(View.VISIBLE);
		}

		LatLon loc = getDefaultLocation();
		LatLon position = plugin.getParkingPosition();
		boolean limited =  plugin.getParkingType();
		String parking_name = limited ?
				getString(R.string.parking_place_limited) : getString(R.string.parking_place);
		if (limited) {
			long endtime = plugin.getParkingTime();
			long currTime = Calendar.getInstance().getTimeInMillis();
			String time = getFormattedTime(endtime - currTime);
			((TextView)mainView.findViewById(R.id.time_left)).setText(time);
			mainView.findViewById(R.id.left_lbl).setVisibility(View.VISIBLE);
		} else {
			((TextView)mainView.findViewById(R.id.time_left)).setText("");
			mainView.findViewById(R.id.left_lbl).setVisibility(View.GONE);
		}
		((TextView) mainView.findViewById(R.id.name)).setText(parking_name);
		ImageView direction = (ImageView) mainView.findViewById(R.id.direction_icon);
		if (loc != null){
			DashLocationView dv = new DashLocationView(direction, (TextView) mainView.findViewById(R.id.distance), position);
			dv.arrowResId = R.drawable.ic_parking_postion_arrow;
			distances.add(dv);
		}



	}

	String getFormattedTime(long timeInMillis) {
		if (timeInMillis < 0) {
			timeInMillis *= -1;
		}
		StringBuilder timeStringBuilder = new StringBuilder();
		int hours = (int) timeInMillis / (1000 * 60 * 60);
		int miutes = (int) timeInMillis / (1000 * 60 * 60 * 60);
		if (hours > 0) {
			timeStringBuilder.append(hours);
			timeStringBuilder.append(" ");
			timeStringBuilder.append(getResources().getString(R.string.osmand_parking_hour));
		}
		timeStringBuilder.append(" ");
		timeStringBuilder.append(miutes);
		timeStringBuilder.append(" ");
		timeStringBuilder.append(getResources().getString(R.string.osmand_parking_minute));
		return timeStringBuilder.toString();
	}
}
