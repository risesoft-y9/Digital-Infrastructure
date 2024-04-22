toast 2.2.0
===========

Toast is a tiny resource loader for JS and CSS files.

Install
-------

You can pick the minified library or install it with :

```
npm install pyrsmk-toast
bower install toast
```

Syntax
------

There's a big thing to have in mind: resources are loaded asynchronous until a callback is encountered. That said, let's dig in it. The library accept as many parameters as you want of the following types: a string (a resource's URL), an array (a resource's URL and a loading validation callback) or a function (an arbitrary callback).

But some examples are better to understand the whole thing:

```js
// Load one css file for mobiles
toast('css/mobiles.css');

// Load several resources for desktops
if(screen.width > 800) {
    toast(
        'css/screens.css',
        'js/modernizr.js',
        'js/classie.js'
    );
}

// Launch a callback when the CSS has been downloaded, and another when scripts have been downloaded too
toast(
    'css/screens.css',
    function() {
        log('screens.css downloaded');
    },
    'js/modernizr.js',
    'js/classie.js',
    function() {
        log('modernizr & classie downloaded');
    }
);
```

If you need to ensure that a script is fully loaded before another one (per example if you want to load a jQuery plugin, the plugin will throw an error if jQuery is not loaded yet), just put a callback between them.

```js
toast(
    'jquery.js',
    function() {},
    'jquery-plugin.js',
	function() {
		// Use jQuery and its plugin
	}
```

Define resource type explicitly
-------------------------------

Toast is guessing your resource type by its extension. But sometimes, like with Google Fonts, there's no extension at the end of the URL. Then we'll need to set the resource type to help toast to load the resource as expected :

```js
toast('[css]https://fonts.googleapis.com/css?family=Open+Sans');
```

License
-------

Licensed under the [MIT license](http://dreamysource.mit-license.org).
