
(function () {

	function _invoke(wgt, fn) {

		if (wgt._isUnbinded)
			_invoke2(wgt, fn);
		else
			setTimeout(function () {
				_invoke2(wgt, fn);
			}, 200);
	}
	function _invoke2(wgt, fn) {
		var n = wgt.$n();
		if (n) {
			try {
				if (fn === 'stop') {
					n.pause();
					n.currentTime = 0;
				} else
					n[fn]();
			} catch (e) {


			}
		}
	}

var Video =

zkc_video.AdvVideo = zk.$extends(zul.Widget, {
	$define: {


		src: function () {
			this.rerender();
		},


		autoplay: function (v) {
			var n = this.$n();
			if (n) n.autoplay = v;
		},


		preload: function (v) {
			var n = this.$n();
			if (n && v !== undefined) n.preload = v;
		},


		controls: function (v) {
			var n = this.$n();
			if (n) n.controls = v;
		},


		loop: function (v) {
			var n = this.$n();
			if (n) n.loop = v;
		},


		muted: function (v) {
			var n = this.$n();
			if (n) n.muted = v;
		}
	},

	play: function () {
		_invoke(this, 'play');
	},

	stop: function () {
		_invoke(this, 'stop');
	},

	pause: function () {
		_invoke(this, 'pause');
	},
	unbind_: function () {
		this._isUnbinded = true;
		this.stop();
		this.$supers(Video, 'unbind_', arguments);
	},
	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments);
		if (this._autoplay)
			attr += ' autoplay';
		if (this._preload !== undefined)
			attr += ' preload="' + this._preload + '"';
		if (this._controls)
			attr += ' controls';
		if (this._loop)
			attr += ' loop';
		if (this._muted)
			attr += ' muted';
		return attr;
	},
	domContent_: function () {
		var src = this._src,
			length = src.length,
			result = '';
		for (var i = 0; i < length; i++) {
			result += '<source src="' + src[i] + '" type="' + this._MIMEtype(src[i]) + '">';
		}
		return result;
	},
	_MIMEtype: function (name) {
		var start = name.lastIndexOf('.'),
			type = 'wav';
		if (start !== -1) {
			var ext = name.substring(start + 1).toLowerCase();
			if (ext === 'mp4') {
				type = 'mp4';
			} else if (ext === 'ogg') {
				type = 'ogg';
			}
		}
		return 'video/' + type;
	}
});
})();