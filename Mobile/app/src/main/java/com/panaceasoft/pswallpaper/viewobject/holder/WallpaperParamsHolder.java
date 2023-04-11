package com.panaceasoft.pswallpaper.viewobject.holder;

import com.panaceasoft.pswallpaper.utils.Constants;

import java.io.Serializable;

public class WallpaperParamsHolder implements Serializable {

    public String loginUserId;
    public String wallpaperName;
    public String queryTypeFlag;
    public String catId;
    public String keyword;
    public String catName;
    public String type;
    public String isRecommended;
    public String isPortrait;
    public String isLandscape;
    public String isSquare;
    public String colorId;
    public String colorName;
    public String colorCode;
    public String rating_max;
    public String rating_min;
    public String point_max;
    public String point_min;
    public String orderBy;
    public String orderType;
    public String addedUserId;
    public String wallpaperIsPublished = "1";
    public String isWallpaper;
    public String isGif;
    public String isLiveWallpaper;

    public WallpaperParamsHolder getDownloadQueryHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.queryTypeFlag = Constants.DONWLOADQUERY;
        this.catName = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "";
        this.orderType = "";
        this.wallpaperIsPublished = "";
        this.isWallpaper = "";
        this.isGif = "";
        this.isLiveWallpaper = "";
        return this;
    }

    public WallpaperParamsHolder getFavQueryHolder() {

        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.queryTypeFlag = Constants.FAVQUERY;
        this.catName = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "";
        this.orderType = "";
        this.wallpaperIsPublished = "";
        this.isWallpaper = "";
        this.isGif = "";
        this.isLiveWallpaper = "";

        return this;
    }

//    public String getFavouriteKey()
//    {
//        return Config.FAVQUERY;
//    }
//
//    public String getDownloadKey()
//    {
//        return Config.DONWLOADQUERY;
//    }

    public WallpaperParamsHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.queryTypeFlag = "";
        this.catName = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "0";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
    }

    public WallpaperParamsHolder getTrendingHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "touch_count";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getTrendingLiveWallpaperHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "touch_count";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "0";
        this.isGif = "0";
        this.isLiveWallpaper = "1";
        return this;
    }

    public WallpaperParamsHolder getLatestHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getLatestLiveWallpaperHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "0";
        this.isGif = "0";
        this.isLiveWallpaper = "1";
        return this;
    }

    public WallpaperParamsHolder getUploadPhotoHolder(String addedUserId) {
        this.loginUserId = "";
        this.addedUserId = addedUserId;
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "";
        this.isWallpaper = "";
        this.isGif = "";
        this.isLiveWallpaper = "";
        return this;
    }

    public WallpaperParamsHolder getPremiumLatestHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "2";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "";
        this.isGif = "";
        this.isLiveWallpaper = "";
        return this;
    }

    public WallpaperParamsHolder getPortraitHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.queryTypeFlag = "";
        this.catId = "";
        this.catName = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.isPortrait = "1";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getLandscapeHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.queryTypeFlag = "";
        this.catId = "";
        this.catName = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.isLandscape = "1";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getSquareHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.queryTypeFlag = "";
        this.catId = "";
        this.catName = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.isSquare = "1";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getRecommendedHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.isRecommended = "1";
        this.orderBy = "recommended_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getRecommendedLiveWallpaperHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.isRecommended = "1";
        this.orderBy = "recommended_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "0";
        this.isGif = "0";
        this.isLiveWallpaper = "1";
        return this;
    }

    public WallpaperParamsHolder getDownloadHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.isSquare = "";
        this.orderBy = "download_count";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getDownloadLiveWallpaperHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.isSquare = "";
        this.orderBy = "download_count";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "0";
        this.isGif = "0";
        this.isLiveWallpaper = "1";
        return this;
    }

    public WallpaperParamsHolder getWallpaperByCategory(String catId, String catName) {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = catId;
        this.catName = catName;
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "1";
        this.isGif = "0";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getGifHolder() {
        this.loginUserId = "";
        this.addedUserId = "";
        this.wallpaperName = "";
        this.catId = "";
        this.catName = "";
        this.queryTypeFlag = "";
        this.keyword = "";
        this.type = "";
        this.isRecommended = "";
        this.isPortrait = "";
        this.isLandscape = "";
        this.isSquare = "";
        this.colorId = "";
        this.colorName = "";
        this.colorCode = "";
        this.rating_max = "";
        this.rating_min = "";
        this.point_max = "";
        this.point_min = "";
        this.orderBy = "added_date";
        this.orderType = "desc";
        this.wallpaperIsPublished = "1";
        this.isWallpaper = "0";
        this.isGif = "1";
        this.isLiveWallpaper = "0";
        return this;
    }

    public WallpaperParamsHolder getTrendingHolderForSorting() {

        this.orderBy = "touch_count";
        this.orderType = "desc";

        return this;
    }

    public WallpaperParamsHolder getDownloadHolderForSorting() {

        this.orderBy = "download_count";
        this.orderType = "desc";

        return this;
    }

    public WallpaperParamsHolder getLowestRatingHolderForSorting() {

        this.orderBy = "rating_count";
        this.orderType = "asc";

        return this;
    }

    public WallpaperParamsHolder getHighestRatingHolderForSorting() {

        this.orderBy = "rating_count";
        this.orderType = "desc";

        return this;
    }

    public WallpaperParamsHolder getLatestHolderForSorting() {

        this.orderBy = "added_date";
        this.orderType = "desc";

        return this;
    }

    public String getKeyForProductMap() {

        final String FREE = "free";
        final String PREMIUM = "PREMIUM";
        final String RECOMMENDED = "recommended";
        final String PORTRAIT = "portrait";
        final String LANDSCAPE = "landscape";
        final String SQUARE = "square";

        String result = "";

        if (!wallpaperName.isEmpty()) {
            result += wallpaperName + ":";
        }

        if (!queryTypeFlag.isEmpty()) {
            result += queryTypeFlag + ":";
        }

        if (!catId.isEmpty()) {
            result += catId + ":";
        }

        if (!catName.isEmpty()) {
            result += catName + ":";
        }

        if (!keyword.isEmpty()) {
            result += keyword + ":";
        }

        if (!type.isEmpty()) {
            if (type.equals("1")) {
                result += FREE + ":";
            } else {
                result += PREMIUM + ":";
            }
        }

        if (!isRecommended.isEmpty()) {
            result += RECOMMENDED + ":";
        }

        if (!isPortrait.isEmpty()) {
            result += PORTRAIT + ":";
        }

        if (!isLandscape.isEmpty()) {
            result += LANDSCAPE + ":";
        }

        if (!isSquare.isEmpty()) {
            result += SQUARE + ":";
        }

        if (!colorId.isEmpty()) {
            result += colorId + ":";
        }

        if (!colorName.isEmpty()) {
            result += colorName + ":";
        }

        if (!colorCode.isEmpty()) {
            result += colorCode + ":";
        }

        if (!rating_max.isEmpty()) {
            result += rating_max + ":";
        }

        if (!rating_min.isEmpty()) {
            result += rating_min + ":";
        }

        if (!point_max.isEmpty()) {
            result += point_max + ":";
        }

        if (!point_min.isEmpty()) {
            result += point_min + ":";
        }

        if (!orderBy.isEmpty()) {
            result += orderBy + ":";
        }

        if (!orderType.isEmpty()) {
            result += orderType + ":";
        }

        if (!wallpaperIsPublished.isEmpty()) {
            result += wallpaperIsPublished + ":";
        }

        if (!isGif.isEmpty()) {
            result += isGif + ":";
        }

        if (!isLiveWallpaper.isEmpty()) {
            result += isLiveWallpaper + ":";
        }
        return result;
    }

}
