package de.thwildau.delightful;

import com.light.controll.LampControll;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.thwildau.tools.SupplyItemDetailFragment;
import de.thwildau.tools.SupplyItemListFragment;
import de.thwildau.tools.UserData;

/**
 * An activity representing a list of SupplyItemList. This activity has
 * different presentations for handset and tablet-size devices. On handsets, the
 * activity presents a list of items, which when touched, lead to a
 * {@link SupplyItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SupplyItemListFragment} and the item details (if present) is a
 * {@link SupplyItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SupplyItemListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class SupplyItemListActivity extends FragmentActivity implements
SupplyItemListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	TextView mainMenuHeaderTextView;
	private Button logoutButton;
	private LampControll LC = LampControll.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_supply);

		if (findViewById(R.id.supplyitem_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((SupplyItemListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.supplyitem_list))
					.setActivateOnItemClick(true);
		}

		mainMenuHeaderTextView = (TextView) findViewById(R.id.supply_header);
		Typeface header_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/ITCEDSCR.TTF");
		mainMenuHeaderTextView.setTypeface(header_typeface);

		logoutButton = (Button) findViewById(R.id.supply_logout_button);
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(v == logoutButton)
					UserData.logout(SupplyItemListActivity.this, UserData.getUser(SupplyItemListActivity.this));

			}
		});

		//set first 
		onItemSelected("1");
	}
	@Override
	public void onBackPressed() {
		LC .closeAllLamps();
		super.onBackPressed();
	}
	/**
	 * Callback method from {@link SupplyItemListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(SupplyItemDetailFragment.ARG_ITEM_ID, id);
			SupplyItemDetailFragment fragment = new SupplyItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.supplyitem_detail_container, fragment)
			.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					SupplyItemDetailActivity.class);
			detailIntent.putExtra(SupplyItemDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
