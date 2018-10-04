package spinc.ar_coreSample.otm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.angelbroking.financialplanning.AppContext;
import com.angelbroking.financialplanning.BuildConfig;
import com.angelbroking.financialplanning.R;
import com.angelbroking.financialplanning.constants.CleverTapEvents;
import com.angelbroking.financialplanning.goal.fragment.GoalBaseFragment;
import com.angelbroking.financialplanning.managers.CleverTapManager;
import com.angelbroking.financialplanning.managers.HttpInvokerV2;
import com.angelbroking.financialplanning.managers.TradeLoginManager;
import com.angelbroking.financialplanning.utils.ApplicationUtils;
import com.angelbroking.financialplanning.utils.DialogUtils;
import com.angelbroking.financialplanning.utils.MarshmallowUtils;
import com.angelbroking.financialplanning.utils.ObjectUtils;
import com.bumptech.glide.Glide;
import com.library_dsign_sdk.threadExecuter.AppExecutors;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class OtmFragment extends GoalBaseFragment implements View.OnClickListener {

    private static final String TAG = "OtmFragment";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CAMERA_CROP = 5;
    //@formatter:off
    @BindView(R.id.imageViewBenefit1)
    AppCompatImageView imageViewBenefit1;
    @BindView(R.id.textViewBenefit1)
    AppCompatTextView textViewBenefit1;
    @BindView(R.id.imageViewBenefit2)
    AppCompatImageView imageViewBenefit2;
    @BindView(R.id.textViewBenefit2)
    AppCompatTextView textViewBenefit2;
    @BindView(R.id.imageViewBenefit3)
    AppCompatImageView imageViewBenefit3;
    @BindView(R.id.textViewBenefit3)
    AppCompatTextView textViewBenefit3;
    @BindView(R.id.llExpandedRiskInvestment)
    LinearLayout llExpandedRiskInvestment;
    @BindView(R.id.textViewKnowMore)
    AppCompatTextView textViewKnowMore;
    @BindView(R.id.llOtmFirstScreen)
    LinearLayout llOtmFirstScreen;
    @BindView(R.id.llOtmSecondScreen)
    LinearLayout llOtmSecondScreen;
    @BindView(R.id.imageViewSignature)
    AppCompatImageView imageViewSignature;
    @BindView(R.id.buttonOtmContinue)
    AppCompatButton buttonOtmContinue;
    @BindView(R.id.llOtmSubmittedRoot)
    LinearLayout llOtmSubmittedRoot;
    @BindView(R.id.llScrollview_otm)
    NestedScrollView llScrollviewOtm;
    @BindView(R.id.buttonProceedOtm)
    AppCompatButton buttonProceedOtm;
    @BindView(R.id.buttonEnableOtm)
    AppCompatButton buttonEnableOtm;
    @BindView(R.id.buttonUseThis)
    AppCompatButton buttonUseThis;
    @BindView(R.id.buttonTakeNewPhoto)
    AppCompatButton buttonTakeNewPhoto;

    //@formatter:on
    @BindView(R.id.llBottomSAubmitAndTakePhoto)
    LinearLayout llBottomSAubmitAndTakePhoto;
    @BindView(R.id.tvAmount_otmSubmitted)
    AppCompatTextView tvAmount_otmSubmitted;
    private String pictureImagePath = "";
    private String signatureFilePath = "";
    private AppExecutors appExecutors = new AppExecutors();
    private String mandateId;
    private AlertDialog dialogConfirmDelete;


    public OtmFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OtmFragment newInstance(String mandateId) {
        OtmFragment fragment = new OtmFragment();
        Bundle args = new Bundle();
        args.putString("MandateId", mandateId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mandateId = getArguments().getString("MandateId");
        }
    }

    @Override
    protected int getLayoutFileName() {
        return R.layout.fragment_otm;
    }

    @Override
    protected void initializeResources() {
        buttonEnableOtm.setOnClickListener(this);
        buttonProceedOtm.setOnClickListener(this);
        textViewKnowMore.setOnClickListener(this);
        buttonTakeNewPhoto.setOnClickListener(this);
        buttonUseThis.setOnClickListener(this);

        showFirstOtmScreen();
    }

    @Override
    protected void attachClickListeners() {

    }

    @Override
    protected int getToolBarTitle() {
        return R.string.otmTitle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void takeCameraPicture() {
        if (MarshmallowUtils.isMarshmallowDevice()) {
            OtmFragmentPermissionsDispatcher.openCameraWithPermissionCheck(this);
        } else {
            openCamera();
        }
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String imageFileName = "Signature" + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);

        if (file.exists()) {
            file.delete();
        }

        file = new File(pictureImagePath);

        Uri outputFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
        }
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForPermission() {
        ApplicationUtils.showToast(AppContext.getInstance(), getString(R.string.info_enable_external_storage_permission));
        if (MarshmallowUtils.isMarshmallowDevice()) {
            MarshmallowUtils.startSettingsActivity(getActivity());
        }
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton("Allow", (dialog, which) -> openCamera())
                .setNegativeButton("Deny", (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        OtmFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * @param isActionEnable
     */

    private void showEnableOrProceedOtmDialog(boolean isActionEnable) {

        if (dialogConfirmDelete == null || (dialogConfirmDelete != null && !dialogConfirmDelete.isShowing())) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_otm, null, false);

            AppCompatButton btn_yes_otmDialog = (AppCompatButton) view.findViewById(R.id.btn_yes_otmDialog);

            dialogConfirmDelete = DialogUtils.dialogWithNoTitle(getActivity(), view);
            dialogConfirmDelete.setCanceledOnTouchOutside(true);
            dialogConfirmDelete.getWindow().setBackgroundDrawableResource(android.R.color.white);
            WindowManager.LayoutParams lp = dialogConfirmDelete.getWindow().getAttributes();
            lp.dimAmount = 0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
            lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialogConfirmDelete.getWindow().setAttributes(lp);

            btn_yes_otmDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogConfirmDelete.dismiss();
                }
            });

            dialogConfirmDelete.setCancelable(true);
            dialogConfirmDelete.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonProceedOtm:
                showEnableOtmSAcreen();

                CleverTapManager.sendOtmEvents(getActivity(), CleverTapEvents.OTM_INTRO);

                break;
            case R.id.buttonEnableOtm:
                takeCameraPicture();
                CleverTapManager.sendOtmEvents(getActivity(), CleverTapEvents.OTM_STEPS);
                break;
            case R.id.textViewKnowMore:
                showEnableOrProceedOtmDialog(false);
                break;

            case R.id.buttonUseThis:
                new UploadFileTask(signatureFilePath).execute();
                break;
            case R.id.buttonTakeNewPhoto:
                openCamera();
                break;
            case R.id.buttonOtmContinue:
                CleverTapManager.sendOtmEvents(getActivity(), CleverTapEvents.OTM_INTRO);
                getActivity().finish();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_CAMERA:

                File imgFile = new File(pictureImagePath);

                if (imgFile.exists()) {

                    showTakePictureOtmSAcreen();

                    Bitmap imageBitmapCamera = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (ObjectUtils.isNotNull(imageBitmapCamera)) {
                        imageBitmapCamera = rotateImage(pictureImagePath, imageBitmapCamera);
                    }

                    if (ObjectUtils.isNotNull(imageBitmapCamera)) {
                        imageViewSignature.setImageBitmap(imageBitmapCamera);
                    }

                    Uri tempUri = getImageUri(getActivity(), imageBitmapCamera);

                    if (ObjectUtils.isNotNull(tempUri)) {

                        File finalFile = new File(pictureImagePath);

                        if (finalFile != null && !TextUtils.isEmpty(finalFile.getAbsolutePath())) {
                            signatureFilePath = finalFile.getAbsolutePath();
                        }
                        if (finalFile != null && !TextUtils.isEmpty(finalFile.getAbsolutePath())) {
                            signatureFilePath = finalFile.getAbsolutePath();
                        }

                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(tempUri, "image/*");
                        startActivityForResult(getCropIntent(intent), CAMERA_CROP);

                    }

                }
                break;

            case CAMERA_CROP:
                if (data != null) {
                    Uri uri = data.getData();

                    if (ObjectUtils.isNotNull(uri)) {
                        signatureFilePath = getRealPathFromURI(uri);

                        if (isAdded()) {
                            Glide.with(getActivity())
                                    .load(uri)
                                    .asBitmap()
                                    .into(imageViewSignature);
                        }
                    }
                }
                break;
        }

    }

    private Intent getCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        return intent;
    }

    private String uploadFileToServer(String filePath) {

        String url = BuildConfig.XSIP_FILE_UPLOAD_URL + SERVICE_UPLOAD_FILE_QSIP;

        List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("ClienCode", TradeLoginManager.getTradingId()));

        String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
        url += paramString;

        HttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost defaultHttpPost = new HttpPost(url);


        // compression

        try {
            File file = new File(filePath);

            int file_size = ObjectUtils.getIntFromString(String.valueOf(file.length() / 1024));

            if (file.exists()) {
                InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), file.length());
                reqEntity.setContentType("application/octet-stream");
                defaultHttpPost.setEntity(reqEntity);
            }

            HttpResponse response = defaultHttpClient.execute(defaultHttpPost);
            long st = System.currentTimeMillis();
            HttpEntity entity = response.getEntity();
            String data = ApplicationUtils.convertStreamToString(entity.getContent());
            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "signatureImage", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";

        if (ObjectUtils.isNotNull(uri)) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

            if (ObjectUtils.isNotNull(cursor)) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
            }
        }

        return path;
    }


    public String compressImage(String filePath) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[32 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();

            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public void requestCreateMandate(String filePath) {

        String url = BuildConfig.XSIP_FILE_UPLOAD_URL + SERVICE_XSIP_SIGNATURE_MISMATCH_MANDATE_CREATION;

        new NetworkCall(url, filePath).callApi();
    }

    /**
     * Screen to show for the FirstTime
     */
    private void showFirstOtmScreen() {
        llBottomSAubmitAndTakePhoto.setVisibility(View.GONE);
        llOtmSubmittedRoot.setVisibility(View.GONE);
        buttonEnableOtm.setVisibility(View.GONE);
        llOtmSecondScreen.setVisibility(View.GONE);
        imageViewSignature.setVisibility(View.GONE);
        buttonProceedOtm.setVisibility(View.VISIBLE);
        llOtmFirstScreen.setVisibility(View.VISIBLE);
    }

    /**
     * Sreen to SHOW the details, when click proceed
     */
    private void showEnableOtmSAcreen() {
        llOtmSecondScreen.setVisibility(View.VISIBLE);
        buttonEnableOtm.setVisibility(View.VISIBLE);
        llBottomSAubmitAndTakePhoto.setVisibility(View.GONE);
        llOtmSubmittedRoot.setVisibility(View.GONE);
        imageViewSignature.setVisibility(View.GONE);
        buttonProceedOtm.setVisibility(View.GONE);
        llOtmFirstScreen.setVisibility(View.GONE);
    }

    /**
     * On cllick on Enable OTM, screen to take Picture and Crop
     */
    private void showTakePictureOtmSAcreen() {

        llBottomSAubmitAndTakePhoto.setVisibility(View.VISIBLE);
        imageViewSignature.setVisibility(View.VISIBLE);

        llOtmSecondScreen.setVisibility(View.GONE);
        buttonEnableOtm.setVisibility(View.GONE);
        llOtmSubmittedRoot.setVisibility(View.GONE);
        buttonProceedOtm.setVisibility(View.GONE);
        llOtmFirstScreen.setVisibility(View.GONE);
    }

    /**
     * Once OTM submitted Successfully
     */
    private void showOtmSubmittedScreen() {

        llBottomSAubmitAndTakePhoto.setVisibility(View.GONE);
        imageViewSignature.setVisibility(View.GONE);

        llOtmSecondScreen.setVisibility(View.GONE);
        buttonEnableOtm.setVisibility(View.GONE);
        buttonProceedOtm.setVisibility(View.GONE);
        llOtmFirstScreen.setVisibility(View.GONE);

        llOtmSubmittedRoot.setVisibility(View.VISIBLE);
        buttonOtmContinue.setOnClickListener(this);

        tvAmount_otmSubmitted.setText(AppContext.getInstance().getResources().getString(R.string.Rs_symbol) + " 50,000");
    }

    private Bitmap cropAndGivePointedShape(Bitmap originalBitmap) {
        Bitmap bmOverlay = Bitmap.createBitmap(originalBitmap.getWidth(),
                originalBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        canvas.drawRect(0, 0, 20, 20, p);

        Point a = new Point(0, 20);
        Point b = new Point(20, 20);
        Point c = new Point(0, 40);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, p);

        a = new Point(0, 40);
        b = new Point(0, 60);
        c = new Point(20, 60);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, p);

        canvas.drawRect(0, 60, 20, originalBitmap.getHeight(), p);

        return bmOverlay;
    }


    private class NetworkCall {

        private String response = "";
        private String url = "";
        private String filePath = "";

        public NetworkCall(String url, String filePath) {
            this.url = url;
            this.filePath = filePath;
        }

        public void callApi() {

            appExecutors.mainThread().execute(() -> {

                showMainProgress();

                appExecutors.networkIO().execute(() -> {

                    try {

                        JSONObject jsonRequest = new JSONObject();
                        jsonRequest.put("ClientCode", TradeLoginManager.getPartyCode());
                        jsonRequest.put("MANDATEID", mandateId);
                        jsonRequest.put("IMAGEPATH", filePath);
                        jsonRequest.put("USERTYPE", "awa");

                        String request = url + " " + jsonRequest.toString();

                        response = HttpInvokerV2.executeGenericPost(AppContext.getInstance(),
                                url,
                                CONTENT_TYPE_APPLICATION_JSON,
                                PROTOCOL_HTTPS,
                                jsonRequest.toString(),
                                REQUEST_BODY_TYPE_JSON,
                                30000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    appExecutors.mainThread().execute(() -> {

                        hideMainProgress();

                        try {
                            if (!TextUtils.isEmpty(response)) {
                                JSONObject obj = new JSONObject(response);
                                if (ObjectUtils.isNotNull(obj)) {
                                    if (obj.has("Message")) {
                                        String msg = obj.optString("Message");
                                        if (!TextUtils.isEmpty(msg) && msg.equalsIgnoreCase("SUCCESS")) {
                                            getActivity().finish();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            });
        }
    }

    private class UploadFileTask extends AsyncTask<Void, Void, Void> {
        private String absolutePath, response = "";

        public UploadFileTask(String absolutePath) {
            this.absolutePath = absolutePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showMainProgress();
        }


        @Override
        protected Void doInBackground(Void... voids) {

            response = uploadFileToServer(absolutePath);
            ApplicationUtils.log("response upload", response);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideMainProgress();

            if (!TextUtils.isEmpty(response)) {
                try {

                    CleverTapManager.sendOtmEvents(getActivity(), CleverTapEvents.OTM_IMG_UPLOAD);

                    JSONObject obj = new JSONObject(response);

                    if (ObjectUtils.isNotNull(obj)) {
                        if (obj.has("FILEPATH")) {
                            String filePath = obj.optString("FILEPATH");

                            if (!TextUtils.isEmpty(filePath)) {
                                requestCreateMandate(filePath);
                                showOtmSubmittedScreen();
                            }
                        }
                       /* if (obj.has("Message")) {
                            ApplicationUtils.showToast(AppContext.getInstance(), obj.optString("Message"));
                        }*/
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public Bitmap rotateImage(String imagePath, Bitmap bitmap) {

        if (!TextUtils.isEmpty(imagePath)) {
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            int angle = 0;

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
                    return rotatedBitmap;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
        }
        return null;
    }
}