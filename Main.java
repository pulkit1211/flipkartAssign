import java.util.*;

public class SocialNetwork {
    private List<User> users;
    private User currentUser;
    private int nextUserId;
    private int nextFeedItemId;
    
    public SocialNetwork() {
        this.users = new ArrayList<>();
        this.nextUserId = 1;
        this.nextFeedItemId = 1;
    }
    
    public void signup(String username, String password) {
        User user = new User(nextUserId++, username, password);
        users.add(user);
    }
    
    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public void post(String text) {
        if (currentUser != null) {
            FeedItem feedItem = new FeedItem(nextFeedItemId++, text, currentUser);
            currentUser.addFeedItem(feedItem);
        }
    }
    
    public void follow(String username) {
        if (currentUser != null) {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    currentUser.follow(user);
                }
            }
        }
    }
    
    public void reply(int feedItemId, String text) {
        FeedItem feedItem = getFeedItem(feedItemId);
        if (feedItem != null && currentUser != null) {
            Comment comment = new Comment(nextFeedItemId++, text, currentUser);
            feedItem.addComment(comment);
        }
    }
    
    public void upvote(int feedItemId) {
        FeedItem feedItem = getFeedItem(feedItemId);
        if (feedItem != null && currentUser != null) {
            feedItem.upvote(currentUser);
        }
    }
    
    public void downvote(int feedItemId) {
        FeedItem feedItem = getFeedItem(feedItemId);
        if (feedItem != null && currentUser != null) {
            feedItem.downvote(currentUser);
        }
    }
    
    public List<FeedItem> getNewsFeed(String sortBy) {
        List<FeedItem> newsFeed = new ArrayList<>();
        
        if (currentUser != null) {
            List<FeedItem> followedItems = new ArrayList<>();
            for (User followedUser : currentUser.getFollowedUsers()) {
                followedItems.addAll(followedUser.getFeedItems());
            }
            switch (sortBy) {
                case "score":
                    newsFeed.addAll(sortByScore(followedItems));
                    break;
                case "comments":
                    newsFeed.addAll(sortByComments(followedItems));
                    break;
                case "timestamp":
                    newsFeed.addAll(sortByTimestamp(followedItems));
                    break;
                default:
                    newsFeed.addAll(followedItems);
                    break;
            }
        }
        
        newsFeed.sort(new Comparator<FeedItem>() {
            @Override
            public int compare(FeedItem f1, FeedItem f2) {
                switch (sortBy) {
                    case "score":
                        return Integer.compare(f2.getScore(), f1.getScore());
                    case "comments":
                        return
                        Integer.compare(f2.getCommentCount(), f1.getCommentCount());
                    case "timestamp":
                        return f2.getTimestamp().compareTo(f1.getTimestamp());
                    default:
                        return 0;
                }
            }
        });
        
        return newsFeed;
    }
    
    private List<FeedItem> sortByScore(List<FeedItem> items) {
        List<FeedItem> sortedItems = new ArrayList<>(items);
        sortedItems.sort(new Comparator<FeedItem>() {
            @Override
            public int compare(FeedItem f1, FeedItem f2) {
                return Integer.compare(f2.getScore(), f1.getScore());
            }
        });
        return sortedItems;
    }
    
    private List<FeedItem> sortByComments(List<FeedItem> items) {
        List<FeedItem> sortedItems = new ArrayList<>(items);
        sortedItems.sort(new Comparator<FeedItem>() {
            @Override
            public int compare(FeedItem f1, FeedItem f2) {
                return Integer.compare(f2.getCommentCount(), f1.getCommentCount());
            }
        });
        return sortedItems;
    }
    
    private List<FeedItem> sortByTimestamp(List<FeedItem> items) {
        List<FeedItem> sortedItems = new ArrayList<>(items);
        sortedItems.sort(new Comparator<FeedItem>() {
            @Override
            public int compare(FeedItem f1, FeedItem f2) {
                return f2.getTimestamp().compareTo(f1.getTimestamp());
            }
        });
        return sortedItems;
    }
    
    private FeedItem getFeedItem(int feedItemId) {
        for (User user : users) {
            for (FeedItem feedItem : user.getFeedItems()) {
                if (feedItem.getId() == feedItemId) {
                    return feedItem;
                }
                for (Comment comment : feedItem.getComments()) {
                    if (comment.getId() == feedItemId) {
                        return feedItem;
                    }
                }
            }
        }
        return null;
    }
    
    public static void main(String[] args) {
        SocialNetwork network = new SocialNetwork();
        
        // Signup users
        network.signup("Alice", "password1");
        network.signup("Bob", "password2");
        network.signup("Charlie", "password3");
        
        // Login as Alice and post a feed item
        network.login("Alice", "password1");
        network.post("Hello, world!");
        
        // Login as Bob and follow Alice
        network.login("Bob", "password2");
        network.follow("Alice");
        
        // Login as Charlie and upvote Alice's feed item
        network.login("Charlie", "password3");
        List<FeedItem> newsFeed = network.getNewsFeed("default");
        FeedItem aliceItem = newsFeed.get(0);
        network.upvote(aliceItem.getId());
        
        // Print the news feed sorted by score
        newsFeed = network.getNewsFeed("score");
        System.out.println("News feed sorted by score:");
        for (FeedItem item : newsFeed) {
            System.out.println(item.toString());
        }
        
        // Post a comment on Alice's feed item
        network.reply(aliceItem.getId(), "Great post!");
        
        // Print the news feed sorted by number of comments
        newsFeed = network.getNewsFeed("comments");
        System.out.println("News feed sorted by number of comments:");
        for (FeedItem item : newsFeed) {
            System.out.println(item.toString());
        }
        
        // Logout
        network.logout();
    }
}
