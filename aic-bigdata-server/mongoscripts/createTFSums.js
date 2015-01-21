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
                tfs[k] = that.value[k] / m;;
            });
			var sum = 0.0;
			Object.keys(tfs).forEach(function(k) {
				sum += tfs[k];
			});
            emit(this._id, sum);
        }
}

db.UserMentionedTopics.mapReduce(
	map,
    function (id, objs) {
        return objs;
    },
    { out: "TFSums"}
);
