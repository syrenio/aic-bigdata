cursor = db.Topics.find({}, {id : true});
topicsList = [];

while (cursor.hasNext()) {
    topicsList.push(cursor.next().id.toLowerCase());
}

print("n..");
var n = db.UserMentionedTopics.count();
print("-> " + n);

function log2(x) {
	return Math.log(x) / Math.LN2;
}

print("counting..");
var idfs = {};
topicsList.forEach(function (t) {
	var name = "value." + t;
	print(name);
	var query = {};
	query[name] = { $gt : 0 };
	idfs[t] = log2(n / db.UserMentionedTopics.find(query).count());
	print("-> " + idfs[t]);
});

print("mapReduce..");

function map() {
        var occurences = [];
        var that = this;
        Object.keys(this.value).forEach(function (k) {
            occurences.push(that.value[k]);
        });
        var m = Math.max.apply(Math, occurences);
        if (m == 0) {
            emit(this._id, this.value);
        }
        else {
            var tfs = {};
            Object.keys(this.value).forEach(function (k) {
                var tf = that.value[k] / m;
                //if (tf > 0.01 && tf < 0.99) {
                //    print(tf);
                //}
                tfs[k] = tf * idfs[k];
            });
            emit(this._id, tfs);
        }
}

db.UserMentionedTopics.mapReduce(
	map,
    function (id, objs) {
        return objs;
    },
    { out: "tfs" , scope : { idfs: idfs} }
);
