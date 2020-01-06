package com.reham.happycrab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;


public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private AnchorNode anchorNode;
    private ModelAnimator animator;
    private int nextAnimation;
    private FloatingActionButton btnAnim;
    private ModelRenderable animationCrab;
    private TransformableNode transformableNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_sceneform);
        btnAnim = findViewById(R.id.btn_anim);

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                if (animationCrab == null)
                    return;
                // Create Anchor
                Anchor anchor = hitResult.createAnchor();
                if (anchorNode == null) // crab not placed
                {
                    anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                    transformableNode.getScaleController().setMinScale(0.03f);
                    transformableNode.getScaleController().setMaxScale(0.1f);
                    transformableNode.setParent(anchorNode);
                    transformableNode.setRenderable(animationCrab);
                }
            }
        });

        // Add frame update to control button state
        arFragment.getArSceneView().getScene().addOnUpdateListener(new Scene.OnUpdateListener() {
            @Override
            public void onUpdate(FrameTime frameTime) {
                if(anchorNode == null)
                {
                    if(btnAnim.isEnabled())
                    {
                        btnAnim.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorDisabled));
                        btnAnim.setEnabled(false);
                    }
                }else
                {
                    btnAnim.setEnabled(true);
                    btnAnim.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
                }
            }
        });

        btnAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (animator == null || !animator.isRunning())
                {
                    AnimationData data = animationCrab.getAnimationData(nextAnimation);
                    nextAnimation = (nextAnimation +1)%animationCrab.getAnimationDataCount();
                    animator = new ModelAnimator(data, animationCrab);
                    animator.start();
                }
            }
        });

        setupModel();
    }
    private void setupModel()
    {
        ModelRenderable.builder()
                .setSource(this, R.raw.cangrejo)
                .build()
                .thenAccept(renderable -> animationCrab = renderable)
                .exceptionally(throwable ->
                        {
                            Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    return null;
                        });
    }
}
