// find all topics
cursor = db.Topics.find({}, {id : true});
topicsList = [];

while (cursor.hasNext()) {
    topicsList.push(cursor.next().id.toLowerCase());
}

function occurences(text, word) {
	text += "";
	word += "";

	var count = 0;
	var pos = 0;
	while (true) {
		pos = text.indexOf(word, pos);
		if (pos >= 0) {
			count++;
			pos += word.length;
		}
		else {
			break;
		}
	}

	return count;
}

function sumFor(topic, objs) {
	var sum = 0;
	objs.forEach(function (o) {
		sum += o[topic];
	});
	return sum;
}

function map() {
	var counts = {};
	var text = this.text.toLowerCase();
	topics.forEach(function (e) {
		counts[e] = occurences(text, e);
	});

    emit(this.user.id, counts);
}

function reduce(userId, objs) {
	var counts = {};
	topics.forEach(function (e) {
		counts[e] = sumFor(e, objs);
	});
	return counts;
}

db.Tweets.mapReduce(
    map,
	reduce,
    { out: "UserMentionedTopics", scope: {topics: topicsList, occurences: occurences, sumFor: sumFor}}
);
