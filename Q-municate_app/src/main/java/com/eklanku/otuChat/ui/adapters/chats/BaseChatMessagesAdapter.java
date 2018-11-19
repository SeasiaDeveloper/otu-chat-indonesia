package com.eklanku.otuChat.ui.adapters.chats;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.storage.model.ConnectycubeFile;
import com.connectycube.ui.chatmessage.adapter.media.video.thumbnails.VideoThumbnail;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.utils.DateUtils;
import com.eklanku.otuChat.utils.FileUtils;
/*<<<<<<< HEAD
import com.quickblox.chat.model.QBChatDialog;
=======*/
import com.connectycube.chat.model.ConnectycubeChatDialog;
//>>>>>>> origin/feature/migration
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_core.qb.commands.chat.QBUpdateStatusMessageCommand;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.State;
import com.connectycube.ui.chatmessage.adapter.ConnectycubeChatAdapter;
import com.connectycube.users.model.ConnectycubeUser;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BaseChatMessagesAdapter extends ConnectycubeChatAdapter<CombinationMessage> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private static final String TAG = BaseChatMessagesAdapter.class.getSimpleName();
    protected static final int TYPE_REQUEST_MESSAGE = 100;
    protected ConnectycubeUser currentUser;
    protected final BaseActivity baseActivity;
    protected FileUtils fileUtils;
    private ArrayList<String> listWithFailedUrls = new ArrayList<>();

    private DataManager dataManager;
    protected ConnectycubeChatDialog chatDialog;

    BaseChatMessagesAdapter(BaseActivity baseActivity, ConnectycubeChatDialog dialog, List<CombinationMessage> chatMessages) {
        super(baseActivity, chatMessages);
        this.baseActivity = baseActivity;
        chatDialog = dialog;
        currentUser = AppSession.getSession().getUser();
        fileUtils = new FileUtils();
        dataManager = DataManager.getInstance();
    }

    @Override
    public long getHeaderId(int position) {
        CombinationMessage combinationMessage = getItem(position);
        return DateUtils.toShortDateLong(combinationMessage.getCreatedDate());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_chat_sticky_header_date, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;

        TextView headerTextView = (TextView) view.findViewById(R.id.header_date_textview);
        CombinationMessage combinationMessage = getItem(position);
        headerTextView.setText(DateUtils.toTodayYesterdayFullMonthDate(combinationMessage.getCreatedDate()));
    }

    protected void defineTimeStampPosition(TextMessageHolder holder) {
        holder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (holder.itemView.getViewTreeObserver().isAlive())
                    holder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                setDynamicTimeStampPosition(holder);
                return true;
            }
        });
    }

    protected void setDynamicTimeStampPosition(TextMessageHolder holder) {
        int dimenPaddingPixCommon = context.getResources().getDimensionPixelSize(R.dimen.padding_common);
        int paddingCount = 7;
        int dimenPaddingPixSmall = context.getResources().getDimensionPixelSize(R.dimen.density_4);

        TextView msgTextView = holder.messageTextView;
        int countLine = msgTextView.getLineCount();

        Layout textLayout = msgTextView.getLayout();
        int msgLastLineWidth = getLastLineWidth(textLayout);

        holder.timeTextMessageTextView.measure(0, 0);
        int timeStampWidth = holder.timeTextMessageTextView.getMeasuredWidth();

        View signView = holder.itemView.findViewById(R.id.message_status_image_view);
        signView.measure(0, 0);
        int signWidth = signView.getMeasuredWidth();

        int itemTextViewWidth = holder.itemView.getWidth();


        int timeStampWidthWithSign = timeStampWidth + signWidth;
        int timeStampWidthWithSignWithPadding = timeStampWidthWithSign + dimenPaddingPixCommon * paddingCount + dimenPaddingPixSmall;

        int limit = itemTextViewWidth - timeStampWidthWithSignWithPadding;

        RelativeLayout layout = holder.itemView.findViewById(R.id.msg_relative_list_item_right);
        layout.setPadding(dimenPaddingPixSmall, 0, 0, 0);

        View timeSignView = holder.itemView.findViewById(R.id.msg_timestamp_sign_widget_bottom);

        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.msg_linear_list_item_right);
        relativeLayoutParams.setMargins(dimenPaddingPixSmall, 0, dimenPaddingPixCommon, 0);

        RelativeLayout.LayoutParams relativeLayoutParamsForTextLinear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayoutParamsForTextLinear.setMargins(0, 0, 0, 0);

        LinearLayout linearTextView = holder.itemView.findViewById(R.id.msg_linear_list_item_right);
        linearTextView.setMinimumWidth(timeStampWidthWithSign + dimenPaddingPixCommon);

        if (msgLastLineWidth >= limit) {
            // timestamp below
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            relativeLayoutParams.addRule(RelativeLayout.BELOW, R.id.msg_linear_list_item_right);

        } else {
            //timestamp in row
            if (countLine == 1) {
                relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                relativeLayoutParams.addRule(RelativeLayout.BELOW, R.id.msg_linear_list_item_right);
            } else {
                boolean isTextViewNotFullWidth = msgTextView.getWidth() < limit - dimenPaddingPixCommon;

                if (isTextViewNotFullWidth) {
                    relativeLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT, 0);
                    relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.msg_linear_list_item_right);

                    relativeLayoutParamsForTextLinear.setMargins(0, 0, -dimenPaddingPixCommon * 2, 0);
                }
                relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            }
        }
        if (timeSignView.getParent() != null) {
            ((ViewGroup) timeSignView.getParent()).removeView(timeSignView);
        }
        linearTextView.setLayoutParams(relativeLayoutParamsForTextLinear);
        layout.addView(timeSignView, relativeLayoutParams);
    }

    private int getLastLineWidth(Layout layout) {
        int lastLineWidth = 0;
        if (layout != null) {
            int maxLineCount = layout.getLineCount();
            lastLineWidth = (int) layout.getLineWidth(maxLineCount - 1);
        }
        return lastLineWidth;
    }

    private static void updateBubbleChatRetainedPadding(View view, int resourceID) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(resourceID);
        view.setPadding(left, top, right, bottom);
        //view.setPadding(left, top, right, bottom);
    }

    @Override
    public int getItemViewType(int position) {
        CombinationMessage combinationMessage = getItem(position);
        if (combinationMessage.getNotificationType() != null) {
            return TYPE_REQUEST_MESSAGE;
        }
        return super.getItemViewType(position);
    }

    @Override
    protected RequestListener getRequestListener(MessageViewHolder holder, int position) {
        CombinationMessage chatMessage = getItem(position);

        return new ImageRequestListener((ImageAttachHolder) holder, isIncoming(chatMessage));
    }

    @Override
    public String obtainAvatarUrl(int valueType, CombinationMessage chatMessage) {
        return chatMessage.getDialogOccupant().getUser().getAvatar();
    }

    private void resetAttachUI(ImageAttachHolder viewHolder) {
        setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_bubble_background), View.GONE);
        setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_image_avatar), View.GONE);
    }

    protected void showAttachUI(ImageAttachHolder viewHolder, boolean isIncoming) {
        if (isIncoming) {
            setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_image_avatar), View.VISIBLE);
        }
        setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_bubble_background), View.VISIBLE);
    }

    protected void setMsgTime(TextView timeView, CombinationMessage chatMessage) {
        timeView.setText(getDate(chatMessage.getDateSent()));
    }

    protected void setViewVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public boolean isEmpty() {
        return chatMessages.size() == 0;
    }

    @Override
    protected boolean isIncoming(CombinationMessage chatMessage) {
        return chatMessage.isIncoming(currentUser.getId());
    }

    protected void updateMessageState(CombinationMessage message, ConnectycubeChatDialog dialog) {
        if (!State.READ.equals(message.getState()) && baseActivity.isNetworkAvailable()) {
            message.setState(State.READ);
            Log.d(TAG, "updateMessageState");

            message.setState(State.READ);
            QBUpdateStatusMessageCommand.start(baseActivity, dialog, message, true);
        }
    }

    @Override
/*<<<<<<< HEAD
    protected void onBindViewMsgRightHolder(QBMessagesAdapter.TextMessageHolder holder, CombinationMessage chatMessage, int position) {
=======*/
    protected void onBindViewMsgRightHolder(ConnectycubeChatAdapter.TextMessageHolder holder, CombinationMessage chatMessage, int position) {
//>>>>>>> origin/feature/migration
        int bubbleResource = isPreviousMsgOut(position) ? R.drawable.bg_chat_right_bubble_edgeless : R.drawable.bg_chat_right_bubble;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        super.onBindViewMsgRightHolder(holder, chatMessage, position);
    }

    @Override
/*<<<<<<< HEAD
    protected void onBindViewMsgLeftHolder(QBMessagesAdapter.TextMessageHolder holder, CombinationMessage chatMessage, int position) {
=======*/
    protected void onBindViewMsgLeftHolder(ConnectycubeChatAdapter.TextMessageHolder holder, CombinationMessage chatMessage, int position) {
//>>>>>>> origin/feature/migration
        int bubbleResource = isPreviousMsgIn(position) ? R.drawable.bg_chat_left_buble_edgeless : R.drawable.bg_chat_left_bubble;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
    }

    public void showPhotoAttach(ConnectycubeChatAdapter.MessageViewHolder holder, int position) {
        Pair<String, String> imageUrl = getPairOfImageUrl(position);
        this.showImageByURL(holder, imageUrl, position);
    }

    public Pair<String,String> getPairOfImageUrl(int position) {
        ConnectycubeAttachment attachment = this.getAttach(position);
        String localUrl = attachment.getUrl();
        attachment.setUrl(null);
        String externalUrl = ConnectycubeFile.getPrivateUrlForUID(attachment.getId());
        return new Pair<>(localUrl, externalUrl);
    }

    private void showImageByURL(ConnectycubeChatAdapter.MessageViewHolder holder, Pair<String,String> url, int position) {
        int preferredImageWidth = (int)this.context.getResources().getDimension(com.connectycube.ui.chatmessage.adapter.R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int)this.context.getResources().getDimension(com.connectycube.ui.chatmessage.adapter.R.dimen.attach_image_height_preview);
//
        DrawableRequestBuilder glideRequestBuilder = Glide.with(this.context)
                .load(url.second)
                .listener(this.getRequestListener(holder, position))
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .dontAnimate()
                .error(com.connectycube.ui.chatmessage.adapter.R.drawable.ic_error);

        if(url.first != null && url.first.length() > 0){
            glideRequestBuilder.placeholder( Drawable.createFromPath( url.first ));
        }
        glideRequestBuilder.into(((ConnectycubeChatAdapter.BaseImageAttachHolder)holder).attachImageView);
    }

    @Override
/*<<<<<<< HEAD
    protected void onBindViewAttachRightHolder(QBMessagesAdapter.ImageAttachHolder holder, CombinationMessage chatMessage, int position) {
=======*/
    protected void onBindViewAttachRightHolder(ConnectycubeChatAdapter.ImageAttachHolder holder, CombinationMessage chatMessage, int position) {
//>>>>>>> origin/feature/migration
        int bubbleResource = isPreviousMsgOut(position) ? R.drawable.bg_chat_right_bubble_edgeless : R.drawable.bg_chat_right_bubble;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        holder.attachImageView.setAdjustViewBounds(true);
        super.onBindViewAttachRightHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachLeftHolder(ImageAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        int bubbleResource = isPreviousMsgIn(position) ? R.drawable.bg_chat_left_buble_edgeless : R.drawable.bg_chat_left_bubble;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        holder.attachImageView.setAdjustViewBounds(true);
        super.onBindViewAttachLeftHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachRightAudioHolder(AudioAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        int bubbleResource = isPreviousMsgOut(position) ? R.drawable.bg_chat_right_bubble_edgeless : R.drawable.bg_chat_right_bubble;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        super.onBindViewAttachRightAudioHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachLeftAudioHolder(AudioAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        int bubbleResource = isPreviousMsgIn(position) ? R.drawable.bg_chat_left_dark_edgeless : R.drawable.bg_chat_left_dark;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        super.onBindViewAttachLeftAudioHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachRightVideoHolder(VideoAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        int bubbleResource = isPreviousMsgOut(position) ? R.drawable.bg_chat_right_bubble_edgeless : R.drawable.bg_chat_right_bubble;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        super.onBindViewAttachRightVideoHolder(holder, chatMessage, position);
    }

    protected void displayAttachmentVideo(ConnectycubeChatAdapter.MessageViewHolder holder, int position) {
        ConnectycubeAttachment attachment = this.getAttach(position);
        int duration = this.getDurationFromAttach(attachment, position);
        this.setDurationVideo(duration, holder);
        String url = this.getVideoUrl(position);
        String localPath = null;
        if(attachment.getUrl() != null && !TextUtils.isEmpty(attachment.getUrl())){
            localPath = attachment.getUrl();
        }
        Pair<String,String> pair = new Pair<>(localPath, url);
        this.showVideoThumbnail(holder, pair, position);
    }

    private void showVideoThumbnail(ConnectycubeChatAdapter.MessageViewHolder holder, Pair<String, String> pairOfUrls, int position)
    {
        int preferredImageWidth = (int)this.context.getResources().getDimension(com.connectycube.ui.chatmessage.adapter.R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int)this.context.getResources().getDimension(com.connectycube.ui.chatmessage.adapter.R.dimen.attach_image_height_preview);
        VideoThumbnailModelChild model = new VideoThumbnailModelChild(pairOfUrls.second);
        ImageView imageView = ((ConnectycubeChatAdapter.VideoAttachHolder)holder).attachImageView;
        if (listWithFailedUrls.contains(model.getPath()))
        {
            //list contains url that was failed to load, so we displaying error icon
            Glide.clear(imageView);
            imageView.setAdjustViewBounds(false);
            ((VideoAttachHolder)holder).playIcon.setVisibility(View.GONE);
            Glide.with(this.context).load(R.drawable.ic_error).override(preferredImageWidth, preferredImageHeight)
                    .dontTransform().dontAnimate().into(imageView);
            return;
        }
        if (pairOfUrls.first != null && !TextUtils.isEmpty(pairOfUrls.first)
                && new File(pairOfUrls.first).exists())
        {
            Glide.with(this.context)
                    .load(Uri.fromFile(new File(pairOfUrls.first)))
                    .asBitmap().override(preferredImageWidth, preferredImageHeight)
                    .dontTransform()
                    .error(com.connectycube.ui.chatmessage.adapter.R.drawable.ic_error)
                    .listener(new RequestListener<Uri, Bitmap>()
                    {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource)
                        {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource)
                        {
                            if (holder != null && holder instanceof ConnectycubeChatAdapter.VideoAttachHolder)
                            {
                                ((ConnectycubeChatAdapter.VideoAttachHolder)holder).playIcon.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }
                    })
                    .into(new BitmapImageViewTarget(imageView)
                    {
                        @Override
                        protected void setResource(Bitmap bitmap)
                        {
                            resizeBitmapAndSetIntoAttachImageView(bitmap, imageView);
                        }
                    });
        }
        else
        {
            Glide.with(this.context)
                    .load(model)
                    .asBitmap()
                    .placeholder(R.drawable.placeholder_attach_video_left)
                    .listener(new VideoThumbnailLoadListener((ConnectycubeChatAdapter.VideoAttachHolder)holder))
                    .override(preferredImageWidth, preferredImageHeight)
                    .dontTransform()
                    .error(com.connectycube.ui.chatmessage.adapter.R.drawable.ic_error)
                    .into(new BitmapImageViewTarget(imageView)
                    {
                        @Override
                        protected void setResource(Bitmap bitmap)
                        {
                            resizeBitmapAndSetIntoAttachImageView(bitmap, imageView);
                        }
                    });
        }
    }

    private void resizeBitmapAndSetIntoAttachImageView(Bitmap bitmap, ImageView imageView)
    {
        Bitmap resized = null;
        //resizing bitmap, if it so big
        int maxSideLength = bitmap.getHeight() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getHeight();
        if (maxSideLength > 1000)
        {
            maxSideLength = 1000;
            int scaledWidth, scaledHeight;
            if (bitmap.getHeight() > bitmap.getWidth())
            {
                scaledHeight = maxSideLength;
                scaledWidth = (int)(((double)bitmap.getWidth() / (double)bitmap.getHeight()) * maxSideLength);
            }
            else
            {
                scaledWidth = maxSideLength;
                scaledHeight = (int)(((double)bitmap.getHeight() / (double)bitmap.getWidth()) * maxSideLength);
            }
            //creating resized Bitmap with maximum side length = 1000px
            resized = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
        }
        if (resized != null)
        {
            bitmap = resized;
        }
        int margin = 0;
        Bitmap centerCrop;
        //cropping bitmap to square, if it have horizontal orientation
        if (bitmap.getHeight() > bitmap.getWidth())
        {
            margin = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            centerCrop = Bitmap.createBitmap(bitmap, 0, margin, bitmap.getWidth(), bitmap.getWidth());
        }
        //uncomment code below if we want to crop bitmap to square, if it have vertical orientation
//        else if (bitmap.getWidth() > bitmap.getHeight())
//        {
//            margin = (bitmap.getWidth() - bitmap.getHeight()) / 2;
//            centerCrop = Bitmap.createBitmap(bitmap, margin, 0, bitmap.getHeight(), bitmap.getHeight());
//        }
        else
        {
            centerCrop = bitmap;
        }
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(centerCrop);
    }

    //Extending VideoThumbnail class, so now we can get path field from model
    class VideoThumbnailModelChild extends VideoThumbnail{
        String path;
        public VideoThumbnailModelChild(String path)
        {
            super(path);
            this.path = path;
        }
        public String getPath(){
            return path;
        }
    }

    // overriding listener, with custom onException method
    protected class VideoThumbnailLoadListener extends ConnectycubeChatAdapter.ImageLoadListener<VideoThumbnailModelChild, Bitmap> {
        private ConnectycubeChatAdapter.VideoAttachHolder holder;

        protected VideoThumbnailLoadListener(ConnectycubeChatAdapter.VideoAttachHolder holder) {
            super(holder);
            this.holder = holder;
            holder.playIcon.setVisibility(View.GONE);
        }

        public boolean onResourceReady(Bitmap resource, VideoThumbnailModelChild model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
            super.onResourceReady(resource, model, target, isFromMemoryCache, isFirstResource);
            this.holder.playIcon.setVisibility(View.VISIBLE);
            return false;
        }

        @Override
        public boolean onException(Exception e, VideoThumbnailModelChild model, Target<Bitmap> target, boolean isFirstResource)
        {
            //adding this url to list, so next time we can avoid downloading this url again
            listWithFailedUrls.add(model.getPath());
            this.holder.attachImageView.setAdjustViewBounds(false);
            return super.onException(e, model, target, isFirstResource);
        }
    }

    @Override
    protected void onBindViewAttachLeftVideoHolder(VideoAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        int bubbleResource = isPreviousMsgIn(position) ? R.drawable.bg_chat_left_buble_edgeless : R.drawable.bg_chat_left_bubble;
        updateBubbleChatRetainedPadding(holder.bubbleFrame, bubbleResource);
        super.onBindViewAttachLeftVideoHolder(holder, chatMessage, position);
    }

    private boolean isPreviousMsgIn(int position) {
        if (position == 0) {
            return false;
        }
        int viewType = getItemViewType(position - 1);
        return viewType == TYPE_TEXT_LEFT || viewType == TYPE_ATTACH_LEFT || viewType == TYPE_ATTACH_LEFT_AUDIO
                || viewType == TYPE_ATTACH_LEFT_VIDEO;
    }

    private boolean isPreviousMsgOut(int position) {
        if (position == 0) {
            return false;
        }
        int viewType = getItemViewType(position - 1);
        return viewType == TYPE_TEXT_RIGHT || viewType == TYPE_ATTACH_RIGHT || viewType == TYPE_ATTACH_RIGHT_AUDIO
                || viewType == TYPE_ATTACH_RIGHT_VIDEO;
    }

    public void addAllInBegin(List<CombinationMessage> collection) {
        chatMessages.addAll(0, collection);
        notifyItemRangeInserted(0, collection.size());
    }

    public void addAllInEnd(List<CombinationMessage> collection) {
        chatMessages.addAll(collection);
        notifyItemRangeInserted(chatMessages.size() - collection.size(), chatMessages.size());
    }

    public void setList(List<CombinationMessage> collection, boolean notifyDataChanged) {
        chatMessages = collection;
        if (notifyDataChanged) {
            this.notifyDataSetChanged();
        }
    }

    public class ImageRequestListener implements RequestListener<String, GlideBitmapDrawable> {
        private ImageAttachHolder viewHolder;
        private Bitmap loadedImageBitmap;
        private boolean isIncoming;

        public ImageRequestListener(ImageAttachHolder viewHolder, boolean isIncoming) {
            this.viewHolder = viewHolder;
            this.isIncoming = isIncoming;
        }

        @Override
        public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
            updateUIAfterLoading();
            resetAttachUI(viewHolder);
            Log.d(TAG, "onLoadingFailed");
            return false;
        }

        @Override
        public boolean onResourceReady(GlideBitmapDrawable loadedBitmap, String imageUri, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            initMaskedImageView(loadedBitmap.getBitmap());
            fileUtils.checkExistsFile(imageUri, loadedBitmap.getBitmap());
            return false;
        }

        protected void initMaskedImageView(Bitmap loadedBitmap) {
            loadedImageBitmap = loadedBitmap;
            viewHolder.attachImageView.setImageBitmap(loadedImageBitmap);

            showAttachUI(viewHolder, isIncoming);

            updateUIAfterLoading();
        }

        private void updateUIAfterLoading() {
            if (viewHolder.attachmentProgressBar != null) {
                setViewVisibility(viewHolder.attachmentProgressBar, View.GONE);
            }
        }
    }

    protected static class RequestsViewHolder extends MessageViewHolder {
        @Nullable
        @Bind(R.id.message_textview)
        TextView messageTextView;

        @Nullable
        @Bind(R.id.time_text_message_textview)
        TextView timeTextMessageTextView;

        @Nullable
        @Bind(R.id.accept_friend_imagebutton)
        ImageView acceptFriendImageView;

        @Nullable
        @Bind(R.id.divider_view)
        View dividerView;

        @Nullable
        @Bind(R.id.reject_friend_imagebutton)
        ImageView rejectFriendImageView;

        @Nullable
        @Bind(R.id.container)
        View container;

        public RequestsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
