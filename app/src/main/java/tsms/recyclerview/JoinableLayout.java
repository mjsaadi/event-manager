package tsms.recyclerview;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Wraps layout to use in {@link RvJoiner}.
 */
public class JoinableLayout implements RvJoiner.Joinable {

	public interface Callback {

		/**
		 * Runs after layout was inflated (ViewHolder created). You can use it to perform extra
         * initialization, such as, for ex. setting onClick listener. NOTE: if you're using this
         * method to save links to child views for later changing values (for example, text in
         * TextView), you should be aware of RecyclerView behavior - it can recycle your layout
         * and recreate it later, and all your changes will be lost, so ensure you also bind your
         * current data in onInflateComplete callback.
		 * @param view just inflated view
		 * @param parent view parent
		 */
		void onInflateComplete(View view, ViewGroup parent);

	}

	private  JoinableLayout.Adapter mAdapter;
	private int mItemType = 0;

	/**
	 * @param view  layout resource to inflate view
	 * @param itemType type constant, or 0, or other value if you don't need it
	 * @param callback callback if you want to customize view after inflating
	 * @param stableId unique id if you need to use stable id feature (calls setHasStableIds(true)
	 *                  on adapter automatically), or {@link RecyclerView#NO_ID} if no stable ids
	 */
	public JoinableLayout(  View view , int itemType, @Nullable Callback callback,
                          long stableId) {
		mItemType = itemType;
		mAdapter = new Adapter(view , itemType, callback, stableId);
	}

	
	public JoinableLayout(  View view , int itemType, @Nullable Callback callback) {
		this(view , itemType, callback, RecyclerView.NO_ID);
	}

	/**
	 * The same as {@link  (int, int, Callback, long)} with 0 type and without stable id
	 */
	public JoinableLayout(  View view , @Nullable Callback callback) {
		this(view , 0, callback, RecyclerView.NO_ID);
	}

	/**
	 * The same as {@link  (int, int, Callback, long)} with null callback and without
	 * stable id
	 */
	public JoinableLayout(  View view , int itemType) {
		this(view , itemType, null, RecyclerView.NO_ID);
	}

	/**
	 * Simple constructor if you don't need any customization.
	 * The same as {@link  (int, int, Callback, long)} with 0 type, null callback and
	 * without stable id
	 * @param view  layout resource to inflate view
	 */
	public JoinableLayout(  View view ) {
		this(view , 0, null, RecyclerView.NO_ID);
	}

	public boolean isVisible() {
		return mAdapter.isVisible();
	}

	public void setVisible(boolean visible) {
		mAdapter.setVisible(visible);
	}

	@Override
	public RecyclerView.Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public int getTypeCount() {
		return 1;
	}

	@Override
	public int getTypeByIndex(int typeIndex) {
		return mItemType;//doesn't matter index (we have only one type)
	}

	private static class Adapter extends RecyclerView.Adapter<Adapter.LayoutVh> {

		private View mLayoutResId;
		private int mItemType;
		private long mStableId;
		private Callback mCallback;
		private boolean mVisible = true;

		//pass stableId == RecyclerView.NO_ID if stable ids not used
		private Adapter(View view , int itemType, Callback callback, long stableId) {
			mLayoutResId = view ;
			mItemType = itemType;
			mCallback = callback;
			mStableId = stableId;
			setHasStableIds(stableId != RecyclerView.NO_ID);
		}

		@Override
		public LayoutVh onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = mLayoutResId;
			if (mCallback != null) {
				mCallback.onInflateComplete(view, parent);
			}
			return new LayoutVh(view);
		}

		@Override
		public void onBindViewHolder(LayoutVh holder, int position) {}

		@Override
		public int getItemCount() {
			return mVisible ? 1 : 0;
		}

		@Override
		public long getItemId(int position) {
			return mStableId;
		}

		@Override
		public int getItemViewType(int position) {
			return mItemType;
		}

		public boolean isVisible() {
			return mVisible;
		}

		public void setVisible(boolean visible) {
			if (mVisible && !visible) {
				mVisible = false;
				notifyItemRemoved(0);
			} else if (!mVisible && visible) {
				mVisible = true;
				notifyItemInserted(0);
			}
		}

		protected static class LayoutVh extends RecyclerView.ViewHolder {

			public LayoutVh(View itemView) {
				super(itemView);
			}

		}

	}

}
