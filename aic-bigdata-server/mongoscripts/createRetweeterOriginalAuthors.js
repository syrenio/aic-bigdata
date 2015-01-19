db.Tweets.mapReduce(
    function() {
        emit(this.user.id, { arr: [this.retweeted_status.user.id], len: 1});
    },
    function(userId, objs) {
        accum = objs[0];
        for (var i = 1; i < objs.length; i++) {
            accum.arr = accum.arr.concat(objs[i].arr);
        }
        accum.len = accum.arr.length;
        return accum;
    },
    { out: "RetweeterOriginalAuthors", query : { retweeted_status : { $exists : true } } }
)
