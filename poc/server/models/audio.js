var mongoose = require('mongoose');

module.exports = mongoose.model('Audio', {
    _creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    date : {type: Date, default: Date.now}
    file: Blob
});