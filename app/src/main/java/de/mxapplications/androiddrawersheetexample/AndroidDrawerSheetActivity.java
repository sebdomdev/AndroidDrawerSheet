package de.mxapplications.androiddrawersheetexample;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import de.mxapplications.androiddrawersheet.AndroidDrawerSheet;

public class AndroidDrawerSheetActivity extends AppCompatActivity {
    private final static String LOG_TAG="Example Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_drawer_sheet);

        ImageButton topButton = (ImageButton)findViewById(R.id.top_image_button);
        ImageButton leftButton = (ImageButton)findViewById(R.id.left_image_button);
        ImageButton rightButton = (ImageButton)findViewById(R.id.right_image_button);
        ImageButton bottomButton = (ImageButton)findViewById(R.id.bottom_image_button);

        final AndroidDrawerSheet topDrawerSheet = (AndroidDrawerSheet)findViewById(R.id.top_drawer);
        final AndroidDrawerSheet leftDrawerSheet = (AndroidDrawerSheet)findViewById(R.id.left_drawer);
        final AndroidDrawerSheet rightDrawerSheet = (AndroidDrawerSheet)findViewById(R.id.right_drawer);
        final AndroidDrawerSheet bottomDrawerSheet = (AndroidDrawerSheet)findViewById(R.id.bottom_drawer);

        Button rightDrawerCloseButton = (Button)findViewById(R.id.right_drawer_close_button);

        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topDrawerSheet.toggleDrawer();
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(leftDrawerSheet.isDrawerOpen()){
                    leftDrawerSheet.closeDrawer();
                }else{
                    leftDrawerSheet.openDrawer();
                }
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightDrawerSheet.toggleDrawer();
            }
        });
        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDrawerSheet.toggleDrawer();
            }
        });

        rightDrawerCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightDrawerSheet.closeDrawer();
            }
        });

        topDrawerSheet.addOnInteractionListener(new AndroidDrawerSheet.OnInteractionListener() {
            @Override
            public void beforeDrawerClosed() {
                Log.i(LOG_TAG, "Before top drawer is closed");
            }

            @Override
            public void beforeDrawerOpened() {
                Log.i(LOG_TAG, "Before top drawer is opened");
            }

            @Override
            public void afterDrawerClosed() {
                Log.i(LOG_TAG, "After top drawer is closed");
            }

            @Override
            public void afterDrawerOpened() {
                Log.i(LOG_TAG, "After top drawer is opened");
            }
        });

        leftDrawerSheet.addOnInteractionListener(new AndroidDrawerSheet.OnInteractionListener() {
            @Override
            public void beforeDrawerClosed() {
                Log.i(LOG_TAG, "Before left drawer is closed");
            }

            @Override
            public void beforeDrawerOpened() {
                Log.i(LOG_TAG, "Before left drawer is opened");
            }

            @Override
            public void afterDrawerClosed() {
                Log.i(LOG_TAG, "After left drawer is closed");
            }

            @Override
            public void afterDrawerOpened() {
                Log.i(LOG_TAG, "After left drawer is opened");
            }
        });

        rightDrawerSheet.addOnInteractionListener(new AndroidDrawerSheet.OnInteractionListener() {
            @Override
            public void beforeDrawerClosed() {
                Log.i(LOG_TAG, "Before right drawer is closed");
            }

            @Override
            public void beforeDrawerOpened() {
                Log.i(LOG_TAG, "Before right drawer is opened");
            }

            @Override
            public void afterDrawerClosed() {
                Log.i(LOG_TAG, "After right drawer is closed");
            }

            @Override
            public void afterDrawerOpened() {
                Log.i(LOG_TAG, "After right drawer is opened");
            }
        });

        bottomDrawerSheet.addOnInteractionListener(new AndroidDrawerSheet.OnInteractionListener() {
            @Override
            public void beforeDrawerClosed() {
                Log.i(LOG_TAG, "Before bottom drawer is closed");
            }

            @Override
            public void beforeDrawerOpened() {
                Log.i(LOG_TAG, "Before bottom drawer is opened");
            }

            @Override
            public void afterDrawerClosed() {
                Log.i(LOG_TAG, "After bottom drawer is closed");
            }

            @Override
            public void afterDrawerOpened() {
                Log.i(LOG_TAG, "After bottom drawer is opened");
            }
        });

        topDrawerSheet.addOnResizeListener(new AndroidDrawerSheet.OnResizeListener() {
            @Override
            public void drawerResized(int size) {
                Log.i(LOG_TAG, "top drawer resized: " + size);
            }
        });
        leftDrawerSheet.addOnResizeListener(new AndroidDrawerSheet.OnResizeListener() {
            @Override
            public void drawerResized(int size) {
                Log.i(LOG_TAG, "left drawer resized: "+size);
            }
        });
        rightDrawerSheet.addOnResizeListener(new AndroidDrawerSheet.OnResizeListener() {
            @Override
            public void drawerResized(int size) {
                Log.i(LOG_TAG, "right drawer resized: "+size);
            }
        });
        bottomDrawerSheet.addOnResizeListener(new AndroidDrawerSheet.OnResizeListener() {
            @Override
            public void drawerResized(int size) {
                Log.i(LOG_TAG, "bottom drawer resized: "+size);
            }
        });
    }
}
