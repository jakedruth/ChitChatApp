# Chit Chat App
The Chit Chat App is an Android app that acts as a client to a simple social network called Chit Chat. Chit Chat is a chronologically sorted stream of text messages from its users. Each message has a count of likes and dislikes.

This app is capable of displaying recent messages in chronological order, including their timestamps, content, and number of likes & dislikes. It can connect to the Chit Chat Server to retrieve messages, post a message, and like or dislike a message. The server responds in JSON.

Each message on Chit Chat has a unique _id property associated with it. This is what the client uses to distinguish each message from other posts and for incrementing the number of likes or dislikes. The client identifies itself when posting messages to Chit Chat by a client name.

The client should never display the same post twice in the feed (unless someone posted it twice). Chit Chat posts are only one level deep. There is no concept of “comments.” This client includes a “refresh” option. It allows the user to post new messages, as well as like & dislike posts. A user should not be able to like or dislike a post multiple times, and the number of likes or dislikes should increment after a user presses the appropriate button.

The client does all of these network connections to the server asynchronously. Additionally, the feed refreshes after the user posts a message.
