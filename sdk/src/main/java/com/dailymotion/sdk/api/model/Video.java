package com.dailymotion.sdk.api.model;

import java.util.ArrayList;

public class Video {
    public String id;
    public String thumbnail_60_url;
    public String thumbnail_120_url;
    public String thumbnail_240_url;
    public String thumbnail_360_url;
    public String thumbnail_480_url;
    public String thumbnail_720_url;
    public String title;
    public String owner;
    public String owner$screenname;
    public String owner$username;
    public int owner$videos_total;
    public int owner$views_total;
    public int owner$playlists_total;
    public int views_total;
    public double duration;
    public AccessError access_error;
    public long created_time;
    public String mode;
    public String description;
    public String url;
    public double aspect_ratio = 16f / 9f;
    public String stream_h264_url;
    public String stream_h264_hq_url;
    public String stream_h264_hd_url;
    public String stream_h264_ld_url;
    public String channel;
    public String channel$name;
    public String owner$avatar_60_url;
    public String owner$avatar_120_url;
    public String owner$avatar_240_url;
    public String owner$avatar_360_url;
    public String owner$avatar_480_url;
    public String owner$avatar_720_url;
    public boolean owner$verified;
    public boolean ads = true;
    public boolean onair = false;
    public boolean sync_allowed = true;
    public String updated_time;
    public ArrayList<String> sources;
    public String live_publish_url;
    public boolean repost;
}
