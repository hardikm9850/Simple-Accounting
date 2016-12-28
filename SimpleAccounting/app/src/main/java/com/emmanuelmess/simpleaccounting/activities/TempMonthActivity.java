package com.emmanuelmess.simpleaccounting.activities;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.emmanuelmess.simpleaccounting.MainActivity;
import com.emmanuelmess.simpleaccounting.R;
import com.emmanuelmess.simpleaccounting.db.TableGeneral;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
/**
 * @author Emmanuel
 */
public class TempMonthActivity extends ListActivity {

	private TableGeneral f;
	private MonthListAdapter monthListAdapter;
	private ArrayList<Integer[]> dateIntValues = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temp_month);

		AppCompatCallback callback = new AppCompatCallback() {
			@Override
			public void onSupportActionModeStarted(ActionMode actionMode) {
			}

			@Override
			public void onSupportActionModeFinished(ActionMode actionMode) {
			}

			@Nullable
			@Override
			public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
				return null;
			}
		};

		AppCompatDelegate delegate = AppCompatDelegate.create(this, callback);

		delegate.onCreate(savedInstanceState);
		delegate.setContentView(R.layout.activity_temp_month);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		delegate.setSupportActionBar(toolbar);
		ActionBar ab = delegate.getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);


		f = new TableGeneral(this);


		(new AsyncTask<Void, Void, int[][]>() {
			@Override
			protected int[][] doInBackground(Void... p) {
				return f.getMonthsWithData();
			}

			@Override
			protected void onPostExecute(int[][] existingMonths) {
				ArrayList<String[]> monthListData = new ArrayList<>();

				for (int d[] : existingMonths) {
					int m = d[0];
					monthListData.add(new String[]{getString(MainActivity.MONTH_STRINGS[m]), String.valueOf(d[1])});
					dateIntValues.add(new Integer[]{m, d[1]});
				}

				int currentM = Integer.parseInt(new SimpleDateFormat("M", Locale.getDefault()).format(new Date())) - 1;
				//YEARS ALREADY START IN 0!!!
				int currentY = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));

				if (dateIntValues.size() == 0
						|| !Arrays.equals(dateIntValues.get(dateIntValues.size() - 1), new Integer[]{currentM, currentY})) {
					monthListData.add(new String[]{getString(MainActivity.MONTH_STRINGS[currentM]), String.valueOf(currentY)});
					dateIntValues.add(new Integer[]{currentM, currentY});
				}


				monthListAdapter = new MonthListAdapter(getApplicationContext(),
						monthListData.toArray(new String[monthListData.size()][2]));
				setListAdapter(monthListAdapter);
			}
		}).execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				onBackPressed();//To make the MainActivity not destroy itself
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		MainActivity.setDate(dateIntValues.get(position)[0],dateIntValues.get(position)[1]);
		onBackPressed();

		/*
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		Bundle extra = new Bundle();
		extra.putInt(MainActivity.MONTH, dateIntValues.get(position)[0]);
		extra.putInt(MainActivity.YEAR, dateIntValues.get(position)[1]);
		intent.putExtras(extra);
		startActivity(intent);
		*/
	}

	private class MonthListAdapter extends ArrayAdapter<String[]> {

		String[][] values;

		MonthListAdapter(Context context, String[][] values) {
			super(context, R.layout.list_item, values);
			this.values = values;
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, @NonNull ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getApplicationContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if(convertView == null)
				convertView = inflater.inflate(R.layout.list_item, parent, false);

			TextView monthView = (TextView) convertView.findViewById(R.id.textMonth);
			TextView yearView = (TextView) convertView.findViewById(R.id.textYear);
			monthView.setText(values[position][0]);
			yearView.setText(values[position][1]);

			return convertView;
		}
	}
}