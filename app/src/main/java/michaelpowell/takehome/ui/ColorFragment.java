package michaelpowell.takehome.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import michaelpowell.takehome.R;

public class ColorFragment extends Fragment {

  ImageView mColorImage;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_color, container, false);
    mColorImage = (ImageView) rootView.findViewById(R.id.color_image);
    return rootView;
  }

  public void setColor(int color) {
    mColorImage.setBackgroundColor(color);
  }
}
