package com.xiaopo.flying.stickerview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.freedrawview.FreeDrawView;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.sticker.ZoomIconEvent;
import com.xiaopo.flying.stickerview.util.FileUtil;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
    implements RadioGroup.OnCheckedChangeListener, ColorListAdapter.IColorListAction,
    SeekBar.OnSeekBarChangeListener {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final int PERM_RQST_CODE = 110;
  private StickerView stickerView;
  private TextSticker sticker;
  private RadioGroup radioGroup;
  private LinearLayout brushLayout;
  private HorizontalScrollView horizontalScrollView;
  private FreeDrawView freeDrawView;
  private ColorListAdapter mColorAdapter;
  private RecyclerView recyclerView;

  public int[] mPaintColors = {
      Color.BLACK, Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.WHITE, Color.RED, Color.GREEN,
      Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA
  };

  private static final int THICKNESS_STEP = 1;
  private static final int THICKNESS_MAX = 30;
  private static final int THICKNESS_MIN = 1;
  private SeekBar mThicknessBar;
  private PaintModeView mPaintModeView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mPaintModeView = (PaintModeView) findViewById(R.id.pmv_edit_image_paint);

    mColorAdapter = new ColorListAdapter(this, mPaintColors, this);
    recyclerView = (RecyclerView) findViewById(R.id.rv_color_list);
    LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(this);
    stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    recyclerView.setLayoutManager(stickerListLayoutManager);
    recyclerView.setAdapter(mColorAdapter);

    brushLayout = (LinearLayout) findViewById(R.id.layout_paint_option);
    freeDrawView = (FreeDrawView) findViewById(R.id.free_draw_view);
    horizontalScrollView = (HorizontalScrollView) findViewById(R.id.view_text_layout);
    (radioGroup = (RadioGroup) findViewById(R.id.radio_selection)).setOnCheckedChangeListener(this);
    stickerView = (StickerView) findViewById(R.id.sticker_view);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    recyclerView.setHasFixedSize(false);
    (mThicknessBar = (SeekBar) findViewById(R.id.slider_thickness)).setOnSeekBarChangeListener(
        this);
    mThicknessBar.setMax((THICKNESS_MAX - THICKNESS_MIN) / THICKNESS_STEP);
    mThicknessBar.setProgress((int) freeDrawView.getPaintWidth());

    BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
        com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
        BitmapStickerIcon.LEFT_TOP);
    deleteIcon.setIconEvent(new DeleteIconEvent());

    BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
        com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp),
        BitmapStickerIcon.RIGHT_BOTOM);
    zoomIcon.setIconEvent(new ZoomIconEvent());

    stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon));

    //default icon layout
    //stickerView.configDefaultIcons();

    stickerView.setBackgroundColor(Color.WHITE);
    stickerView.setLocked(false);
    stickerView.setConstrained(true);

    sticker = new TextSticker(this);

    sticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
        R.drawable.sticker_transparent_background));
    sticker.setText(
        "Hello, world!kjha[hjapnh]ahashjahjajhsjh]pajhpajhsjaphjshjpajhpjhpasjhjhajshpjsahpjashjasjhasjhahj");
    sticker.setTextColor(Color.BLACK);
    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
    sticker.resizeText();

    stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
      @Override public void onStickerAdded(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerAdded");
      }

      @Override public void onStickerClicked(@NonNull Sticker sticker) {
        //stickerView.removeAllSticker();
        if (sticker instanceof TextSticker) {
          TextSticker textSticker = (TextSticker) sticker;
          //stickerView.replace(sticker);
          //stickerView.invalidate();
          showTextDialogue(textSticker.getText().toString(), textSticker);
        }
        Log.d(TAG, "onStickerClicked");
      }

      @Override public void onStickerDeleted(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerDeleted");
      }

      @Override public void onStickerDragFinished(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerDragFinished");
      }

      @Override public void onStickerZoomFinished(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerZoomFinished");
      }

      @Override public void onStickerFlipped(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerFlipped");
      }

      @Override public void onStickerDoubleTapped(@NonNull Sticker sticker) {
        Log.d(TAG, "onDoubleTapped: double tap will be with two click");
      }
    });

    if (toolbar != null) {
      toolbar.setTitle(R.string.app_name);
      toolbar.inflateMenu(R.menu.menu_save);
      toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override public boolean onMenuItemClick(MenuItem item) {
          if (item.getItemId() == R.id.item_save) {
            File file = FileUtil.getNewFile(MainActivity.this, "Sticker");
            if (file != null) {
              stickerView.save(file);
              Toast.makeText(MainActivity.this, "saved in " + file.getAbsolutePath(),
                  Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(MainActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
            }
          }
          //                    stickerView.replace(new DrawableSticker(
          //                            ContextCompat.getDrawable(MainActivity.this, R.drawable.haizewang_90)
          //                    ));
          return false;
        }
      });
    }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERM_RQST_CODE);
    } else {
      //loadSticker();
    }
  }

  private void loadSticker() {
    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.haizewang_215);
    Drawable drawable1 = ContextCompat.getDrawable(this, R.drawable.haizewang_23);
    stickerView.addSticker(new DrawableSticker(drawable));
    stickerView.addSticker(new DrawableSticker(drawable1),
        Sticker.Position.BOTTOM | Sticker.Position.RIGHT);

    Drawable bubble = ContextCompat.getDrawable(this, R.drawable.bubble);
    stickerView.addSticker(new TextSticker(getApplicationContext()).setDrawable(bubble)
        .setText("Sticker\n")
        .setMaxTextSize(18)
        .resizeText(), Sticker.Position.TOP);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERM_RQST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      //loadSticker();
    }
  }

  public void undoPaint(View view) {
    freeDrawView.undoLast();
  }

  public void testReplace(View view) {
    if (stickerView.replace(sticker)) {
      Toast.makeText(MainActivity.this, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(MainActivity.this, "Replace Sticker failed!", Toast.LENGTH_SHORT).show();
    }
  }

  public void testLock(View view) {
    stickerView.setLocked(!stickerView.isLocked());
  }

  public void testRemove(View view) {
    if (stickerView.removeCurrentSticker()) {
      Toast.makeText(MainActivity.this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
          .show();
    } else {
      Toast.makeText(MainActivity.this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
          .show();
    }
  }

  public void testRemoveAll(View view) {
    stickerView.removeAllStickers();
  }

  public void testAdd(View view) {
    final TextSticker sticker = new TextSticker(this);
    sticker.setText("Values are present inside the material");
    sticker.setTextColor(Color.BLUE);
    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
    sticker.resizeText();
    sticker.setMaxTextSize(20);
    stickerView.addSticker(sticker);
  }

  @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
    switch (radioGroup.getCheckedRadioButtonId()) {
      case R.id.radio_color:
        freeDrawView.setEnabled(true);
        brushLayout.setVisibility(View.VISIBLE);
        horizontalScrollView.setVisibility(View.GONE);
        stickerView.setLocked(true);
        break;
      case R.id.radio_typo:
        freeDrawView.setEnabled(false);
        horizontalScrollView.setVisibility(View.VISIBLE);
        brushLayout.setVisibility(View.GONE);

        break;
    }
  }

  @Override public void onColorSelected(int position, int color) {
    freeDrawView.setPaintColor(color);
    mPaintModeView.setBackgroundColor(color);
  }

  @Override public void onMoreSelected(int position) {

  }

  @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    freeDrawView.setPaintWidthPx(
        convertDpToPixel(this, (THICKNESS_MIN + (progress * THICKNESS_STEP))));
  }

  @Override public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {

  }

  public static float convertDpToPixel(Context context, float dp) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return px;
  }

  public void showTextDialogue(String input, final TextSticker textSticker) {
    final AlertDialog builder = new AlertDialog.Builder(this).create();
    View inflate = View.inflate(this, R.layout.dialog_edit_text_annotation, null);
    builder.setView(inflate);
    RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.rv_color_list);
    final PaintModeView paintModeView =
        (PaintModeView) inflate.findViewById(R.id.pmv_edit_image_paint);
    LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(this);
    stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    recyclerView.setLayoutManager(stickerListLayoutManager);
    paintModeView.setPaintStrokeColor(Color.BLACK);
    ColorListAdapter.IColorListAction iColorListAction = new ColorListAdapter.IColorListAction() {
      @Override public void onColorSelected(int position, int color) {
        paintModeView.setPaintStrokeColor(Color.BLACK);
      }

      @Override public void onMoreSelected(int position) {

      }
    };
    final EditText editText = (EditText) inflate.findViewById(R.id.input);

    ColorListAdapter mColorAdapter = new ColorListAdapter(this, mPaintColors, iColorListAction);
    recyclerView.setAdapter(mColorAdapter);
    editText.setText(input);
    inflate.findViewById(R.id.btn_photo_annotation_submit)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            String inputValue = editText.getText().toString();
            textSticker.setText(inputValue);
            textSticker.setTextColor(paintModeView.getStokenColor());
            textSticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
            textSticker.resizeText();
            textSticker.setMaxTextSize(18);
            stickerView.replace(textSticker);
            stickerView.invalidate();
            builder.dismiss();
          }
        });
    inflate.findViewById(R.id.btn_photo_annotation_cancel)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            builder.dismiss();
          }
        });
    builder.show();
  }
}
