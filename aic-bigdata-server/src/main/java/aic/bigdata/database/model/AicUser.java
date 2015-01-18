package aic.bigdata.database.model;

import java.util.Date;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Users")
public class AicUser implements User {

	@DatabaseField(id = true)
	private long id;
	// @DatabaseField()
	// private RateLimitStatus rateLimitStatus;
	@DatabaseField()
	private int accessLevel;
	@DatabaseField(width = 500)
	private String biggerProfileImageURL;
	@DatabaseField(width = 500)
	private String biggerProfileImageURLHttps;
	@DatabaseField()
	private Date createdAt;
	@DatabaseField()
	private String description;
	@DatabaseField()
	private boolean verified;
	@DatabaseField()
	private boolean translator;
	@DatabaseField()
	private boolean showAllInlineMedia;
	@DatabaseField()
	private boolean isProtectedField;
	@DatabaseField(width = 500)
	private boolean profileUseBackgroundImage;
	@DatabaseField(width = 500)
	private boolean profileBackgroundTiled;
	@DatabaseField()
	private boolean geoEnabled;
	@DatabaseField()
	private boolean followRequestSent;
	@DatabaseField()
	private boolean contributorsEnabled;
	@DatabaseField()
	private int utcOffset;
	// @DatabaseField()
	// private URLEntity urlEntity;
	@DatabaseField(width = 500)
	private String url;
	@DatabaseField()
	private String timeZone;
	@DatabaseField()
	private int statusesCount;
	// @DatabaseField()
	// private Status status;
	@DatabaseField()
	private String screenName;
	@DatabaseField()
	private String profileTextColor;
	@DatabaseField()
	private int favouritesCount;
	// @DatabaseField()
	// private URLEntity[] descriptionURLEntities;
	@DatabaseField()
	private int followersCount;
	@DatabaseField()
	private int friendsCount;
	@DatabaseField()
	private String lang;
	@DatabaseField()
	private int listedCount;
	@DatabaseField()
	private String location;
	@DatabaseField()
	private String miniProfileImageURL;
	@DatabaseField()
	private String miniProfileImageURLHttps;
	@DatabaseField(index = true)
	private String name;
	@DatabaseField()
	private String originalProfileImageURL;
	@DatabaseField()
	private String originalProfileImageURLHttps;
	@DatabaseField()
	private String profileBackgroundColor;
	@DatabaseField(width = 500)
	private String profileBackgroundImageURL;
	@DatabaseField(width = 500)
	private String profileBackgroundImageUrlHttps;
	@DatabaseField()
	private String profileBannerIPadRetinaURL;
	@DatabaseField()
	private String profileBannerIPadURL;
	@DatabaseField()
	private String profileBannerMobileRetinaURL;
	@DatabaseField()
	private String profileBannerMobileURL;
	@DatabaseField()
	private String profileBannerRetinaURL;
	@DatabaseField()
	private String profileBannerURL;
	@DatabaseField()
	private String profileImageURL;
	@DatabaseField()
	private String profileImageURLHttps;
	@DatabaseField()
	private String profileLinkColor;
	@DatabaseField()
	private String profileSidebarBorderColor;
	@DatabaseField()
	private String profileSidebarFillColor;

	public AicUser() {

	}

	public AicUser(User usr) {
		// FIXME CONVERT TwitterUser to AicUser
		accessLevel = usr.getAccessLevel();
		biggerProfileImageURL = usr.getBiggerProfileImageURL();
		biggerProfileImageURLHttps = usr.getBiggerProfileImageURLHttps();
		contributorsEnabled = usr.isContributorsEnabled();
		createdAt = usr.getCreatedAt();
		description = usr.getDescription();
		// descriptionURLEntities = usr.getDescriptionURLEntities();
		favouritesCount = usr.getFavouritesCount();
		followersCount = usr.getFollowersCount();
		followRequestSent = usr.isFollowRequestSent();
		friendsCount = usr.getFriendsCount();
		geoEnabled = usr.isGeoEnabled();
		id = usr.getId();
		isProtectedField = usr.isProtected();
		lang = usr.getLang();
		listedCount = usr.getListedCount();
		location = usr.getLocation();
		miniProfileImageURL = usr.getMiniProfileImageURL();
		miniProfileImageURLHttps = usr.getMiniProfileImageURLHttps();
		name = usr.getName();
		originalProfileImageURL = usr.getOriginalProfileImageURL();
		originalProfileImageURLHttps = usr.getOriginalProfileImageURLHttps();
		profileBackgroundColor = usr.getProfileBackgroundColor();
		profileBackgroundImageURL = usr.getProfileBackgroundImageURL();
		profileBackgroundImageUrlHttps = usr.getProfileBackgroundImageUrlHttps();
		profileBackgroundTiled = usr.isProfileBackgroundTiled();
		profileBannerIPadRetinaURL = usr.getProfileBannerIPadRetinaURL();
		profileBannerIPadURL = usr.getProfileBannerIPadURL();
		profileBannerMobileRetinaURL = usr.getProfileBannerMobileRetinaURL();
		profileBannerMobileURL = usr.getProfileBannerMobileURL();
		profileBannerRetinaURL = usr.getProfileBannerRetinaURL();
		profileBannerURL = usr.getProfileBannerURL();
		profileImageURL = usr.getProfileImageURL();
		profileImageURLHttps = usr.getProfileImageURLHttps();
		profileLinkColor = usr.getProfileLinkColor();
		profileSidebarBorderColor = usr.getProfileSidebarBorderColor();
		profileSidebarFillColor = usr.getProfileSidebarFillColor();
		profileTextColor = usr.getProfileTextColor();
		profileUseBackgroundImage = usr.isProfileUseBackgroundImage();
		// rateLimitStatus = usr.getRateLimitStatus();
		screenName = usr.getScreenName();
		showAllInlineMedia = usr.isShowAllInlineMedia();
		// status = usr.getStatus();
		statusesCount = usr.getStatusesCount();
		timeZone = usr.getTimeZone();
		translator = usr.isTranslator();
		url = usr.getURL();
		// urlEntity = usr.getURLEntity();
		utcOffset = usr.getUtcOffset();
		verified = usr.isVerified();
	}

	@Override
	public int compareTo(User o) {
		int id = new Long(this.getId()).intValue();
		int id2 = new Long(o.getId()).intValue();
		return id - id2;
	}

	@Override
	public int getAccessLevel() {
		return accessLevel;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() {
		// return rateLimitStatus;
		return null;
	}

	@Override
	public String getBiggerProfileImageURL() {
		return biggerProfileImageURL;
	}

	@Override
	public String getBiggerProfileImageURLHttps() {
		return biggerProfileImageURLHttps;
	}

	@Override
	public Date getCreatedAt() {
		return createdAt;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public URLEntity[] getDescriptionURLEntities() {
		return null;
		// return descriptionURLEntities;
	}

	@Override
	public int getFavouritesCount() {
		return favouritesCount;
	}

	@Override
	public int getFollowersCount() {
		return followersCount;
	}

	@Override
	public int getFriendsCount() {
		return friendsCount;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getLang() {
		return lang;
	}

	@Override
	public int getListedCount() {
		return listedCount;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getMiniProfileImageURL() {
		return miniProfileImageURL;
	}

	@Override
	public String getMiniProfileImageURLHttps() {
		return miniProfileImageURLHttps;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalProfileImageURL() {
		return originalProfileImageURL;
	}

	@Override
	public String getOriginalProfileImageURLHttps() {
		return originalProfileImageURLHttps;
	}

	@Override
	public String getProfileBackgroundColor() {
		return profileBackgroundColor;
	}

	@Override
	public String getProfileBackgroundImageURL() {
		return profileBackgroundImageURL;
	}

	@Override
	public String getProfileBackgroundImageUrlHttps() {
		return profileBackgroundImageUrlHttps;
	}

	@Override
	public String getProfileBannerIPadRetinaURL() {
		return profileBannerIPadRetinaURL;
	}

	@Override
	public String getProfileBannerIPadURL() {
		return profileBannerIPadURL;
	}

	@Override
	public String getProfileBannerMobileRetinaURL() {
		return profileBannerMobileRetinaURL;
	}

	@Override
	public String getProfileBannerMobileURL() {
		return profileBannerMobileURL;
	}

	@Override
	public String getProfileBannerRetinaURL() {
		return profileBannerRetinaURL;
	}

	@Override
	public String getProfileBannerURL() {
		return profileBannerURL;
	}

	@Override
	public String getProfileImageURL() {
		return profileImageURL;
	}

	@Override
	public String getProfileImageURLHttps() {
		return profileImageURLHttps;
	}

	@Override
	public String getProfileLinkColor() {
		return profileLinkColor;
	}

	@Override
	public String getProfileSidebarBorderColor() {
		return profileSidebarBorderColor;
	}

	@Override
	public String getProfileSidebarFillColor() {
		return profileSidebarFillColor;
	}

	@Override
	public String getProfileTextColor() {
		return profileTextColor;
	}

	@Override
	public String getScreenName() {
		return screenName;
	}

	@Override
	public Status getStatus() {
		// return status;
		return null;
	}

	@Override
	public int getStatusesCount() {
		return statusesCount;
	}

	@Override
	public String getTimeZone() {
		return timeZone;
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public URLEntity getURLEntity() {
		// return urlEntity;
		return null;
	}

	@Override
	public int getUtcOffset() {
		return utcOffset;
	}

	@Override
	public boolean isContributorsEnabled() {
		return contributorsEnabled;
	}

	@Override
	public boolean isFollowRequestSent() {
		return followRequestSent;
	}

	@Override
	public boolean isGeoEnabled() {
		return geoEnabled;
	}

	@Override
	public boolean isProfileBackgroundTiled() {
		return profileBackgroundTiled;
	}

	@Override
	public boolean isProfileUseBackgroundImage() {
		return profileUseBackgroundImage;
	}

	@Override
	public boolean isProtected() {
		return isProtectedField;
	}

	@Override
	public boolean isShowAllInlineMedia() {
		return showAllInlineMedia;
	}

	@Override
	public boolean isTranslator() {
		return translator;
	}

	@Override
	public boolean isVerified() {
		return verified;
	}

}
