package sk.upjs.ics.android.fotr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import de.ecotastic.android.camerautil.lib.CameraIntentHelperActivity;
import de.ecotastic.android.camerautil.util.BitmapHelper;


public class MainActivity extends CameraIntentHelperActivity {

    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.easyShareAction);
        this.shareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.shareAction:
                sharePhoto();
                return true;       
            default:
                return super.onOptionsItemSelected(item);                
        }
    }

    private void sharePhoto() {
        Intent shareIntent = getShareIntent();

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    private Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setDataAndType(this.photoUri, "image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Fotr Photo");
        shareIntent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
        return shareIntent;
    }

    public void onShootButtonClick(View v) {
        startCameraIntent();
    }

    @Override
    protected void onPhotoUriFound() {
        super.onPhotoUriFound();
        Bitmap photo = BitmapHelper.readBitmap(this, this.photoUri);
        if (photo != null) {
            photo = BitmapHelper.shrinkBitmap(photo, 300, this.rotateXDegrees);
            ImageView imageView = (ImageView) findViewById(R.id.thumbnailImageView);
            imageView.setImageBitmap(photo);
        }

        //Delete photo in second location (if applicable)
        if (this.preDefinedCameraUri != null && !this.preDefinedCameraUri.equals(this.photoUri)) {
            BitmapHelper.deleteImageWithUriIfExists(this.preDefinedCameraUri, this);
        }
        //Delete photo in third location (if applicable)
        if (this.photoUriIn3rdLocation != null) {
            BitmapHelper.deleteImageWithUriIfExists(this.photoUriIn3rdLocation, this);
        }

        updateEasyShare();
    }

    private void updateEasyShare() {
        if(this.shareActionProvider != null) {
            this.shareActionProvider.setShareIntent(getShareIntent());
        }
    }

}
