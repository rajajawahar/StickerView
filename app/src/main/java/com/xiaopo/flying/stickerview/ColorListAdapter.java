package com.xiaopo.flying.stickerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 颜色列表Adapter
 *
 * @author panyi
 */
public class ColorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final Context mContext;

  public interface IColorListAction {
    void onColorSelected(final int position, final int color);

    void onMoreSelected(final int position);
  }

  private int[] colorsData;

  private IColorListAction mCallback;

  public ColorListAdapter(Context context, int[] colors, IColorListAction action) {
    super();
    this.mContext = context;
    this.colorsData = colors;
    this.mCallback = action;
  }

  public class ColorViewHolder extends RecyclerView.ViewHolder {
    View colorPanelView;

    public ColorViewHolder(View itemView) {
      super(itemView);
      this.colorPanelView = itemView.findViewById(R.id.color_panel_view);
    }
  }

  @Override public int getItemCount() {
    return colorsData.length;
  }

  @Override public int getItemViewType(int position) {
    return 0;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    return new ColorViewHolder(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.view_color_panel, parent, false));
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    onBindColorViewHolder((ColorViewHolder) holder, position);
  }

  private void onBindColorViewHolder(final ColorViewHolder holder, final int position) {
    holder.colorPanelView.setBackgroundColor(colorsData[position]);
    holder.colorPanelView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (mCallback != null) {
          mCallback.onColorSelected(position, colorsData[position]);
        }
      }
    });
  }
}
