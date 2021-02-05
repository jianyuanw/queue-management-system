(function (global, factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD
        define(['jquery'], factory);
    } else if (typeof exports === 'object') {
        // CommonJS
        factory(require('jquery'));
    } else {
        // Browser globals
        factory(global.jQuery);
    }
}(this, function ($) {
    'use strict';

    var FloatingLabel = function (element, options) {
        this.$element  = $(element);
        this.$label = this.$element.find('label');
        this.$input = this.$element.find('input, textarea');

        this.options   = $.extend({}, this.getOptions(), options);
        
        this.$label.addClass(this.options.floatingLabelClass);
        this.$input.addClass(this.options.floatingInputClass);
        this.$input.attr('placeholder','');

        if (this.$input.hasClass('form-control-lg')){
            this.$label.addClass('fl-lg');
        }
        else if (this.$input.hasClass('form-control-sm')){
            this.$label.addClass('fl-sm');
        }

        var that = this;
        this.$label.on('click',function(){that.$input.focus();});
        this.$input.on('focus',function () {
            that.$label.addClass(that.options.floatingLabelOnClass)
                .addClass('floating-label-' + that.options.floatingStyle);
            that.$input.addClass('floating-input-' + that.options.floatingStyle);
        });
        this.$input.focusout(function () {
            if (that.$input.val().trim() === ''){
                that.$input.removeClass('floating-input-' + that.options.floatingStyle);
                that.$label.removeClass(that.options.floatingLabelOnClass)
                    .removeClass('floating-label-' + that.options.floatingStyle);
            }
        });

        if (this.$input.val().trim() !== ''){
            this.$input.trigger('focus');
        }
    };

    FloatingLabel.prototype.getOptions = function() {
        return {
            floatingLabelClass: this.$element.attr('data-floating-label-class') || $.fn.floatingLabel.defaults.floatingLabelClass,
            floatingLabelOnClass: this.$element.attr('data-floating-label-on-class') || $.fn.floatingLabel.defaults.floatingLabelOnClass,
            floatingInputClass: this.$element.attr('data-floating-input-class') || $.fn.floatingLabel.defaults.floatingInputClass,
            floatingStyle: this.$element.attr('data-floating-style') || $.fn.floatingLabel.defaults.floatingStyle
        };
    };

    function FloatingLabelPlugin(option) {
        return this.each(function () {
            var options = typeof option == 'object' && option;
            return new FloatingLabel(this, options);
        })
    }

    $.fn.floatingLabel             = FloatingLabelPlugin;
    $.fn.floatingLabel.Constructor = FloatingLabel;
    $.fn.floatingLabel.defaults = {
        floatingLabelClass: 'floating-label',
        floatingLabelOnClass: 'floating-label-on',
        floatingInputClass: 'floating-input',
        floatingStyle: 'primary'
    };
}));
