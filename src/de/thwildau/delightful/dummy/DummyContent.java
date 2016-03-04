package de.thwildau.delightful.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thwildau.tools.Supplies;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
	public static List<DummyItem> KURS_ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "Restaurant", Supplies.SUPPLY_RESTAURANT));
		addItem(new DummyItem("2", "Fuﬂball", Supplies.SUPPLY_FUSSBALL));
		addItem(new DummyItem("3", "Biking", null));
		addItem(new DummyItem("4", "Golf", null));
		addItem(new DummyItem("5", "Wellness", Supplies.SUPPLY_WELLNESS));
		addItem(new DummyItem("6", "Sauna", null));
		addItem(new DummyItem("7", "Hallenbad", null));
		addItem(new DummyItem("8", "Bowling", null));
		addItem(new DummyItem("9", "Kurse", Supplies.SUPPLY_KURSE));
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String content;
		private String[][] supplies;

		public DummyItem(String id, String content, String[][] supplies) {
			this.id = id;
			this.content = content;
			this.supplies = supplies;
		}

		@Override
		public String toString() {
			return content;
		}
		public String[][] getSupplies(){
			return supplies;
		}
	}
}
