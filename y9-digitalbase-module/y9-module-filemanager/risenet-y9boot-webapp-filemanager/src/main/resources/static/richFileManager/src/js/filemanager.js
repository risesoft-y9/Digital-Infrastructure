/**
 *	Rich Filemanager JS core
 *
 *	filemanager.js
 *
 *	@license	MIT License
 *	@author		Jason Huck - Core Five Labs
 *	@author		Simon Georget <simon (at) linea21 (dot) com>
 *	@author		Pavel Solomienko <https://github.com/servocoder/>
 *	@copyright	Authors
 */

(function($) {

$.richFilemanagerPlugin = function(element, pluginOptions)
{
	/**
	 * Plugin's default options
	 */
	var defaults = {
		baseUrl: '.',	// relative path to the FM plugin folder
		configUrl: null,
		config: {},		// configuration options
        callbacks: {
            beforeCreateImageUrl: function (resourceObject, url) {
                return url;
            },
            beforeCreatePreviewUrl: function (resourceObject, url) {
                return url;
            },
			beforeSelectItem: function (resourceObject, url) {
				return url;
			},
			afterSelectItem: function (resourceObject, url, contextWindow) {},
            beforeSetRequestParams: function (requestMethod, requestParams) {
                return requestParams;
            },
            beforeSendRequest: function (requestMethod, requestParams) {
                return true;
            }
		}
	};

	/**
	 * The reference the current instance of the object
	 */
	var fm = this;

	/**
	 * Private properties accessible only from inside the plugin
	 */
	var $container = $(element),	// reference to the jQuery version of DOM element the plugin is attached to
		$wrapper = $container.children('.fm-wrapper'),
		$header = $wrapper.find('.fm-header'),
		$uploader = $header.find('.fm-uploader'),
		$splitter = $wrapper.children('.fm-splitter'),
		$footer = $wrapper.children('.fm-footer'),
		$fileinfo = $splitter.children('.fm-fileinfo'),
		$filetree = $splitter.children('.fm-filetree'),
		$viewItemsWrapper = $fileinfo.find('.view-items-wrapper'),
		$previewWrapper = $fileinfo.find('.fm-preview-wrapper'),
        $viewItems = $viewItemsWrapper.find('.view-items'),
		$uploadButton = $uploader.children('.fm-upload'),

		config = null,				// configuration options
		fileRoot = '/',				// relative files root, may be changed with some query params
		apiConnector = null,		// API connector URL to perform requests to server
		capabilities = [],			// allowed actions to perform in FM
		configSortField = null,		// items sort field name
		configSortOrder = null,		// items sort order 'asc'/'desc'
		fmModel = null,				// filemanager knockoutJS model
		langModel = null,			// language model
        globalize = null,			// formatting and parsing tool (numbers, dates, etc.)
		delayStack = null,			// function execution delay manager

		/** variables to keep request options data **/
		fullexpandedFolder = null,	// path to be automatically expanded by filetree plugin

		/** service variables **/
        _url_ = purl(),
		timeStart = new Date().getTime();

	/**
	 * This holds the merged default and user-provided options.
	 * Plugin's properties will be available through this object like:
	 * - fm.propertyName from inside the plugin
	 * - element.data('richFilemanager').propertyName from outside the plugin, where "element" is the element the plugin is attached to;
	 * @type {{}}
	 */

	// The plugin's final settings, contains the merged default and user-provided options (if any)
    fm.settings = $.extend(true, defaults, pluginOptions);


	/*--------------------------------------------------------------------------------------------------------------
	 Public methods
	 Can be called like:
	 - fm.methodName(arg1, arg2, ... argn) from inside the plugin
	 - element.data('richFilemanager').publicMethod(arg1, arg2, ... argn) from outside the plugin,
	   where "element" is the element the plugin is attached to
	--------------------------------------------------------------------------------------------------------------*/

	fm.write = function(message, obj) {
		var log = alertify;
		var options = $.extend({}, {
			reset: true,
			delay: 5000,
			logMaxItems: 5,
			logPosition: 'bottom right',
			logContainerClass: 'fm-log',
            logMessageTemplate: null,
			parent: document.body,
			onClick: undefined,
			unique: false,
			type: 'log'
		}, obj);

		// display only one log for the specified 'logClass'
		if(options.logClass && options.unique && $('.fm-log').children('.' + options.logClass).length > 0) {
			return log;
		}

		if(options.reset) log.reset();
		log.parent(options.parent);
		log.logDelay(options.delay);
		log.logMaxItems(options.logMaxItems);
		log.logPosition(options.logPosition);
		log.logContainerClass(options.logContainerClass);
		log.logMessageTemplate(options.logMessageTemplate);
		log[options.type](message, options.onClick);

		var logs = log.getLogs();
		return logs[logs.length-1];
	};

	fm.error = function(message, options) {
		return fm.write(message, $.extend({}, {
			type: 'error',
			delay: 10000
		}, options));
	};

	fm.warning = function(message, options) {
		return fm.write(message, $.extend({}, {
			type: 'warning',
			delay: 10000
		}, options));
	};

	fm.success = function(message, options) {
		return fm.write(message, $.extend({}, {
			type: 'success',
			delay: 6000
		}, options));
	};

	fm.alert = function(message) {
		alertify
			.reset()
			.dialogContainerClass('fm-popup')
			.alert(message);
	};

	fm.confirm = function(obj) {
		alertify
			.reset()
			.dialogWidth(obj.width)
			.dialogPersistent(obj.persistent)
			.dialogContainerClass('fm-popup')
			.confirm(obj.message, obj.okBtn, obj.cancelBtn);
	};

	fm.prompt = function(obj) {
		alertify
			.reset()
			.dialogWidth(obj.width)
			.dialogPersistent(obj.persistent)
			.dialogContainerClass('fm-popup')
			.theme(obj.template)
			.prompt(obj.message, obj.value || '', obj.okBtn, obj.cancelBtn);
	};

	fm.dialog = function(obj) {
		alertify
			.reset()
			.dialogWidth(obj.width)
			.dialogPersistent(obj.persistent)
			.dialogContainerClass('fm-popup')
			.dialog(obj.message, obj.buttons);
	};

	// Forces columns to fill the layout vertically.
	// Called on initial page load and on resize.
	fm.setDimensions = function() {
		var padding = $wrapper.outerHeight(true) - $wrapper.height(),
        	newH = $(window).height() - $header.height() - $footer.height() - padding,
            newW = $splitter.width() - $splitter.children('.splitter-bar-vertical').outerWidth() - $filetree.outerWidth();

        $splitter.height(newH);
		$fileinfo.width(newW);
	};

    fm.console = function() {
        if(config.options.logger && arguments) {
            [].unshift.call(arguments, new Date().getTime());
            console.log.apply(this, arguments);
        }
    };

    // Reload currently open folder content
    fm.refreshFolder = function(applyTreeNode) {
        fmModel.loadPath(fmModel.currentPath(), applyTreeNode);
    };

    // Load content of specified relative folder path
    fm.loadFolder = function(path, applyTreeNode) {
	if (path !== '/') {
          path = '/' + trim(path, '/') + '/';
	}
        fmModel.loadPath(path, applyTreeNode);
    };


	/*--------------------------------------------------------------------------------------------------------------
	 Private methods
	 These methods can be called only from inside the plugin like: methodName(arg1, arg2, ... argn)
	--------------------------------------------------------------------------------------------------------------*/

	/**
	 * The "constructor" method that gets called when the object is created
	 */
	var construct = function() {
		var deferred = $.Deferred();

		deferred
			.then(function() {
				return configure();
			})
			.then(function() {
				return localize();
			})
			.then(function(conf_d, conf_u) {
				return performInitialRequest();
			})
			.then(function() {
				return includeTemplates();
			})
			.then(function() {
				includeAssets(function() {
                    initialize();
				});
			});

		deferred.resolve();
	};

	var configure = function() {
		return $.when(loadConfigFile('default'), loadConfigFile('user')).done(function(confd, confu) {
			var config_default = confd[0];
			var config_user = confu[0];

			// remove version from user config file
			if (config_user !== undefined && config_user !== null) {
				delete config_user.version;
			}
			// merge default config and user config file
			config = $.extend({}, config_default, config_user);

			// setup apiConnector
			if(config.api.connectorUrl) {
				apiConnector = config.api.connectorUrl;
			} else {
				var connectorUrl = location.origin + location.pathname;
				var langConnector = 'connectors/' + config.api.lang + '/filemanager.' + config.api.lang;

				// for url like http://site.com/index.html
				if(getExtension(connectorUrl).length > 0) {
					connectorUrl = connectorUrl.substring(0, connectorUrl.lastIndexOf('/') + 1);
				}
				apiConnector = connectorUrl + langConnector;
			}
		});
	};

	// performs initial request to server to retrieve initial params
    var performInitialRequest = function () {
        return buildAjaxRequest('GET', {
            mode: 'initiate'
        }).done(function (response) {
            if (response.data) {
                var serverConfig = response.data.attributes.config;
                // configuration options retrieved from the server
                $.each(serverConfig, function (section, options) {
                    $.each(options, function (param, value) {
                        if (value === null) {
                            return true;
                        }
                        if (config[section] === undefined) {
                            config[section] = [];
                        }
                        config[section][param] = value;
                    });
                });

                // Overrides client-side capabilities list to improve the compatibility for connectors.
				// If a connector doesn't support a new feature, the plugin will mask this feature to avoid error.
                if (serverConfig.options && serverConfig.options.capabilities) {
                    config.options.capabilities = serverConfig.options.capabilities;
				}
            }
        }).fail(function (xhr) {
            fm.error('Unable to perform initial request to server.');
            handleAjaxError(xhr);
        }).then(function (response) {
            if (response.errors) {
                return $.Deferred().reject();
            }
        });
    };

	// localize messages based on configuration or URL value
	var localize = function() {
        langModel = new LangModel();

		return $.ajax()
			.then(function() {
                var urlLangCode = _url_.param('langCode');
				if(urlLangCode) {
					// try to load lang file based on langCode in query params
					return file_exists(langModel.buildLangFileUrl(urlLangCode))
						.done(function() {
                            langModel.setLang(urlLangCode);
						})
						.fail(function() {
							setTimeout(function() {
								fm.error('Given language file (' + langModel.buildLangFileUrl(urlLangCode) + ') does not exist!');
							}, 500);
						});
				} else {
                    langModel.setLang(config.language.default);
				}
			})
			.then(function() {
				// append query param to prevent caching
                var langFileUrl = langModel.buildLangFileUrl(langModel.getLang()) + '?_=' + new Date().getTime();

				return $.ajax({
					type: 'GET',
					url: langFileUrl,
					dataType: 'json'
				}).done(function(jsonTrans) {
                    langModel.setTranslations(jsonTrans);
				});
            })
            .then(function() {
            	// trim language code to first 2 chars
				var lang = langModel.getLang().substr(0, 2),
                    baseUrl = fm.settings.baseUrl;

                return $.when(
                    $.get(baseUrl + '/libs/cldrjs/cldr-dates/' + lang + '/ca-gregorian.json'),
                    $.get(baseUrl + '/libs/cldrjs/cldr-numbers/' + lang + '/numbers.json'),
                    $.get(baseUrl + '/libs/cldrjs/cldr-core/supplemental/likelySubtags.json'),
                    $.get(baseUrl + '/libs/cldrjs/cldr-core/supplemental/timeData.json'),
                    $.get(baseUrl + '/libs/cldrjs/cldr-core/supplemental/weekData.json')
                ).fail(function () {
                    fm.error('CLDR files for "' + lang + '" language do not exist!');
                }).then(function () {
                    // Normalize $.get results, we only need the JSON, not the request statuses.
                    return [].slice.apply(arguments, [0]).map(function (result) {
                        return result[0];
                    });
                }).then(Globalize.load).then(function () {
                    globalize = Globalize(lang);
                });
            });
	};

	var includeTemplates = function() {
		return $.when(loadTemplate('upload-container'), loadTemplate('upload-item')).done(function(uc, ui) {
			var tmpl_upload_container = uc[0];
			var tmpl_upload_item = ui[0];

			$wrapper
				.append(tmpl_upload_container)
				.append(tmpl_upload_item);
		});
	};

	var includeAssets = function(callback) {
		var primary = [],
        	secondary = [];

        // theme defined in configuration file
        primary.push('/themes/' + config.options.theme + '/styles/theme.css');

        if(config.viewer.image.lazyLoad) {
            primary.push('/libs/lazyload/dist/lazyload.min.js');
        }
        if(config.customScrollbar.enabled) {
            primary.push('/libs/custom-scrollbar-plugin/jquery.mCustomScrollbar.min.css');
            primary.push('/libs/custom-scrollbar-plugin/jquery.mCustomScrollbar.concat.min.js');
        }

        // add callback on loaded assets and inject primary ones
        primary.push(callback);
        loadAssets(primary);

		// Loading CodeMirror if enabled for online edition
		if(config.editor.enabled) {
			var editorTheme = config.editor.theme;
            if (editorTheme && editorTheme !== 'default') {
                secondary.push('/libs/CodeMirror/theme/' + editorTheme + '.css');
            }
            secondary.push('/libs/CodeMirror/lib/codemirror.css');
            secondary.push('/libs/CodeMirror/lib/codemirror.js');
            secondary.push('/libs/CodeMirror/addon/selection/active-line.js');
            secondary.push('/libs/CodeMirror/addon/display/fullscreen.css');
            secondary.push('/libs/CodeMirror/addon/display/fullscreen.js');
		}

		// Load Markdown-it, if enabled. For .md to HTML rendering:
		if(config.viewer.markdownRenderer.enabled) {
            secondary.push('/src/css/fm-markdown.css');
            secondary.push('/libs/markdown-it/markdown-it.min.js');
            secondary.push('/libs/markdown-it/default.min.css');
            secondary.push('/libs/markdown-it/highlight.min.js');
            secondary.push('/libs/markdown-it/markdown-it-footnote.min.js');
            secondary.push('/libs/markdown-it/markdown-it-replace-link.min.js');
		}

		if(!config.options.browseOnly) {
			// Loading jquery file upload library
            secondary.push('/src/js/libs-fileupload.js');

			if(config.upload.multiple) {
                secondary.push('/libs/jQuery-File-Upload/css/dropzone.css');
			}
		}

		if(secondary.length) {
            loadAssets(secondary);
		}
	};

	var initialize = function () {
        delayStack = new DelayStack();

		// reads capabilities from config files if exists else apply default settings
		capabilities = config.options.capabilities || ['upload', 'select', 'download', 'rename', 'copy', 'move', 'delete', 'extract', 'createFolder'];

        // If the server is in read only mode, set the GUI to browseOnly:
        if (config.security.readOnly) {
            config.options.browseOnly = true;
        }

        // Set default upload parameter
        if (!config.upload.paramName) {
            config.upload.paramName = 'files';
        }

		// defines sort params
		var chunks = [];
		if(config.options.fileSorting) {
			chunks = config.options.fileSorting.toLowerCase().split('_');
		}

		configSortField = chunks[0] || 'name';
		configSortOrder = chunks[1] || 'asc';

        // changes files root to restrict the view to a given folder
		var exclusiveFolder = _url_.param('exclusiveFolder');
        if(exclusiveFolder) {
            fileRoot = '/' + exclusiveFolder + '/';
            fileRoot = normalizePath(fileRoot);
        }

        // get folder that should be expanded after filemanager is loaded
        var expandedFolder = _url_.param('expandedFolder');
        if(expandedFolder) {
            fullexpandedFolder = fileRoot + expandedFolder + '/';
            fullexpandedFolder = normalizePath(fullexpandedFolder);
        }

		// Activates knockout.js
		fmModel = new FmModel();
		ko.applyBindings(fmModel);

        fmModel.itemsModel.initiateLazyLoad();
        fmModel.filterModel.setName(_url_.param('filter'));

		ko.bindingHandlers.toggleNodeVisibility = {
			init: function (element, valueAccessor) {
				var node = valueAccessor();
				$(element).toggle(node.isExpanded());
			},
			update: function (element, valueAccessor) {
				var node = valueAccessor();
				if(node.isSliding() === false) {
					return false;
				}
				if(node.isExpanded() === false) {
					$(element).slideDown(config.filetree.expandSpeed, function() {
						node.isSliding(false);
						node.isExpanded(true);
					});
				}
				if(node.isExpanded() === true) {
					$(element).slideUp(config.filetree.expandSpeed, function() {
						node.isSliding(false);
						node.isExpanded(false);
					});
				}
			}
		};

		ko.bindingHandlers.draggableView = {
			init: function(element, valueAccessor, allBindingsAccessor) {
                fmModel.ddModel.makeDraggable(valueAccessor(), element);
			}
		};

		ko.bindingHandlers.droppableView = {
			init: function(element, valueAccessor, allBindingsAccessor) {
                fmModel.ddModel.makeDroppable(valueAccessor(), element);
			}
		};

		ko.bindingHandlers.draggableTree = {
			init: function(element, valueAccessor, allBindingsAccessor) {
                fmModel.ddModel.makeDraggable(valueAccessor(), element);
			}
		};

		ko.bindingHandlers.droppableTree = {
			init: function(element, valueAccessor, allBindingsAccessor) {
                fmModel.ddModel.makeDroppable(valueAccessor(), element);
			}
		};

        $wrapper.mousewheel(function(e) {
            if (!fmModel.ddModel.dragHelper) {
                return true;
            }

            var $panes,
                $obstacle = null;

            if (config.customScrollbar.enabled) {
                $panes = $([$viewItemsWrapper[0], $filetree[0]]);
            } else {
                $panes = $splitter.children('.splitter-pane');
            }

            $panes.each(function(i) {
            	var $pane = $(this),
            		top = $pane.offset().top,
            		left = $pane.offset().left;

				if ((e.offsetY >= top && e.offsetY <= top + $pane.height()) &&
					(e.offsetX >= left && e.offsetX <= left + $pane.width())) {
                    $obstacle = $pane;
                    return false;
				}
			});

            // no one appropriate obstacle is overlapped
            if ($obstacle === null) {
                return false;
            }

            if (config.customScrollbar.enabled) {
                var $scrollBar = $obstacle.find('.mCSB_scrollTools_vertical'),
					directionSign = (e.deltaY === 1) ? '+' : '-';

                if ($scrollBar.is(':visible')) {
                    $obstacle.mCustomScrollbar('scrollTo', [directionSign + '=250', 0], {
                        scrollInertia: 500,
                        scrollEasing: 'easeOut',
                        callbacks: true
                    });
                }
            } else {
                if ($obstacle[0].scrollHeight > $obstacle[0].clientHeight) {
                    var scrollPosition = $obstacle.scrollTop();
                    var scrollOffset = scrollPosition - (200 * e.deltaY);

                    fmModel.ddModel.isScrolling = true;
                    scrollOffset = (scrollOffset < 0) ? 0 : scrollOffset;
                    $obstacle.stop().animate({scrollTop: scrollOffset}, 100, 'linear', function() {
                        fmModel.ddModel.isScrolling = false;
                        fmModel.ddModel.isScrolled = true;
                    });
				}
            }
        });

        $viewItems.selectable({
            filter: 'li:not(.directory-parent), tbody > tr:not(.directory-parent)',
            cancel: '.directory-parent, thead',
            disabled: !config.manager.selection.enabled,
            appendTo: $viewItems,
            start: function(event, ui) {
                clearSelection();
                fmModel.itemsModel.isSelecting(true);
            },
            stop: function(event, ui) {
                fmModel.itemsModel.isSelecting(false);
            },
            selected: function(event, ui) {
                var koItem = ko.dataFor(ui.selected);
                koItem.selected(true);
            },
            unselected: function(event, ui) {
                var koItem = ko.dataFor(ui.unselected);
                koItem.selected(false);
            }
        });

        $fileinfo.contextMenu({
            selector: '.view-items',
            zIndex: 10,
            // wrap options with "build" allows to get item element
            build: function ($triggerElement, e) {
                var contextMenuItems = {
                    createFolder: {
                    	name: lg('create_folder'),
						className: 'create-folder'
                    },
                    paste: {
                    	name: lg('clipboard_paste'),
						className: 'paste',
                        disabled: function (key, options) {
							return fmModel.clipboardModel.isEmpty();
                        }
                    }
                };

                if (!fmModel.clipboardModel.enabled() || config.options.browseOnly === true) {
                    delete contextMenuItems.paste;
                }
                if (!hasCapability('createFolder') || config.options.browseOnly === true) {
                    delete contextMenuItems.createFolder;
                }
				// prevent the creation of context menu
                if ($.isEmptyObject(contextMenuItems)) {
                    return false;
				}

                return {
                    appendTo: '.fm-container',
                    items: contextMenuItems,
					reposition: false,
                    callback: function(itemKey, options) {
                        switch(itemKey) {
                            case 'createFolder':
                                fmModel.headerModel.createFolder();
                                break;

                            case 'paste':
                                fmModel.clipboardModel.paste();
                                break;
                        }
                    }
                }
            }
        });

		if(config.extras.extra_js) {
			for(var i=0; i<config.extras.extra_js.length; i++) {
				$.ajax({
					type: 'GET',
					url: config.extras.extra_js[i],
					dataType: 'script',
					async: config.extras.extra_js_async
				});
			}
		}

		// adding a close button triggering callback function if CKEditorCleanUpFuncNum passed
		if(_url_.param('CKEditorCleanUpFuncNum')) {
            fmModel.headerModel.closeButton(true);
            fmModel.headerModel.closeButtonOnClick = function() {
                parent.CKEDITOR.tools.callFunction(_url_.param('CKEditorCleanUpFuncNum'));
			};
		}

        prepareFileTree();
        setupUploader();
        fmModel.treeModel.loadDataNode(fmModel.treeModel.rootNode, true, true);

		// Loading CustomScrollbar if enabled
		if(config.customScrollbar.enabled) {
			$filetree.mCustomScrollbar({
				theme: config.customScrollbar.theme,
				scrollButtons: {
					enable: config.customScrollbar.button
				},
				advanced: {
					autoExpandHorizontalScroll: true,
					updateOnContentResize: true
				},
				callbacks: {
					onScrollStart: function() {
						fmModel.ddModel.isScrolling = true;
					},
					onScroll: function() {
						fmModel.ddModel.isScrolling = false;
					}
				},
				axis: 'yx'
			});

            $previewWrapper.mCustomScrollbar({
                theme: config.customScrollbar.theme,
                scrollButtons: {
                    enable: config.customScrollbar.button
                },
                advanced: {
                    autoExpandHorizontalScroll: true,
                    updateOnContentResize: true,
                    updateOnSelectorChange: '.fm-preview-viewer'
                }
			});

            $viewItemsWrapper.mCustomScrollbar({
                theme: config.customScrollbar.theme,
                scrollButtons: {
                    enable: config.customScrollbar.button
                },
                advanced: {
                    autoExpandHorizontalScroll:true,
                    updateOnContentResize: true,
                    updateOnSelectorChange: '.grid, .list'
                },
                callbacks: {
                    onScrollStart: function() {
                        if (!fmModel.itemsModel.continiousSelection()) {
                            this.yStartPosition = this.mcs.top;
                            this.yStartTime = (new Date()).getTime();
                        }
                        fmModel.ddModel.isScrolling = true;
                    },
                    onScroll: function() {
                    	fmModel.ddModel.isScrolling = false;
                        fmModel.ddModel.isScrolled = true;
                    },
                    whileScrolling: function() {
                        if (config.manager.selection.enabled) {
                            // would prefer to get scroll position from [onScrollStart],
                            // but the [onScroll] should fire first, which happens with a big lag
                            var timeDiff = (new Date()).getTime() - this.yStartTime;

                            // check if selection lasso has not been dropped while scrolling
                            if (!fmModel.itemsModel.continiousSelection() && timeDiff > 400) {
                                this.yStartPosition = this.mcs.top;
                            }

                            // set flag if selection lasso is active
                            if (fmModel.itemsModel.isSelecting()) {
                                fmModel.itemsModel.continiousSelection(true);
                            }

                            var yIncrement = Math.abs(this.mcs.top) - Math.abs(this.yStartPosition);
                            $viewItems.selectable('repositionCssHelper', yIncrement, 0);
                        }

                        if (fmModel.itemsModel.lazyLoad) {
                            fmModel.itemsModel.lazyLoad.handleScroll(); // use throttle
                        }
                    }
                },
                axis: 'y',
                alwaysShowScrollbar: 0
            });
        }

		// add useragent string to html element for IE 10/11 detection
		var doc = document.documentElement;
		doc.setAttribute('data-useragent', navigator.userAgent);

		if(config.options.logger) {
			var timeEnd = new Date().getTime();
			var time = timeEnd - timeStart;
			console.log('Total execution time : ' + time + ' ms');
		}

		var $loading = $container.find('.fm-loading-wrap');
        // remove loading screen div
		$loading.fadeOut(800, function() {
            fm.setDimensions();
		});
        fm.setDimensions();
	};

    /**
	 * Language model
	 *
     * @constructor
     */
    var LangModel = function() {
        var currentLang = null,
            translationsHash = {},
            translationsPath = fm.settings.baseUrl + '/languages/';

        this.buildLangFileUrl = function(code) {
            return translationsPath + code + '.json';
        };

        this.setLang = function(code) {
            currentLang = code;
        };

        this.getLang = function() {
            return currentLang;
        };

        this.setTranslations = function(json) {
            translationsHash = json;
        };

        this.getTranslations = function() {
            return translationsHash;
        };

        this.translate = function(key) {
            return translationsHash[key];
        };
    };

    /**
	 * DelayStack
	 * Delays execution of functions that is passed as argument
	 *
     * @constructor
     */
    var DelayStack = function() {
    	var hash = {},
    		delay_stack = this;

        this.push = function(name, callback, ms) {
            delay_stack.removeTimer(name);
            hash[name] = setTimeout(callback, ms);
        };

        this.getTimer = function(name) {
            return hash[name];
        };

        this.removeTimer = function(name) {
        	if (hash[name]) {
                clearTimeout(hash[name]);
                delete hash[name];
			}
        };
    };

	/**
	 * Knockout general model
	 *
	 * @constructor
	 */
	var FmModel = function() {
		var model = this;
		this.config = ko.observable(config);
		this.loadingView = ko.observable(true);
		this.previewFile = ko.observable(false);
		this.viewMode = ko.observable(config.manager.defaultViewMode);
		this.currentPath = ko.observable(fileRoot);
		this.browseOnly = ko.observable(config.options.browseOnly);
        this.previewModel = ko.observable(null);
        this.currentLang = langModel.getLang();
        this.lg = langModel.getTranslations();

        this.previewFile.subscribe(function (enabled) {
            if (!enabled) {
                // close editor upon disabling preview
                model.previewModel.closeEditor();

                // update content of descriptive panel
                if (model.itemsModel.descriptivePanel.rdo().id === model.previewModel.rdo().id) {
                    model.itemsModel.descriptivePanel.render(model.previewModel.viewer.content());
                }
            }
        });

        this.isCapable = function(capability) {
            return hasCapability(capability);
        };

        this.loadPath = function (targetPath, applyTreeNode) {
            var targetNode,
                folderLoader = new FolderAjaxLoader(targetPath);

            if (applyTreeNode) {
                targetNode = fmModel.treeModel.findByParam('id', targetPath);
            }
            if (targetNode) {
                folderLoader.setPreloader(fmModel.treeModel.getPreloader(targetNode))
            }

            folderLoader
                .setPreloader(model.itemsModel.getPreloader())
                .setDataHandler(function (resourceObjects, targetPath) {
                    if (targetNode) {
                        fmModel.treeModel.addNodes(resourceObjects, targetNode, true);
                    }
                    model.itemsModel.addItems(resourceObjects, targetPath, true);
                    model.searchModel.clearInput();
                })
                .load(function () {
                    return readFolder(targetPath);
                });
        };

        this.addElements = function (resourceObjects, targetPath, reset) {
            // handle tree nodes
            var targetNode = model.treeModel.findByParam('id', targetPath);
            if (targetNode) {
                model.treeModel.addNodes(resourceObjects, targetNode, reset);
            }

            // handle view objects
            if (model.currentPath() === targetPath) {
                model.itemsModel.addItems(resourceObjects, targetPath, reset);
            }
        };

        this.removeElement = function (resourceObject) {
            // handle tree nodes
            var treeNode = model.treeModel.findByParam('id', resourceObject.id);
            if (treeNode) {
                treeNode.remove();
            }

            // handle view objects
            var viewItem = model.itemsModel.findByParam('id', resourceObject.id);
            if (viewItem) {
                viewItem.remove();
            }
        };

        // fetch selected view items OR tree nodes
        this.fetchSelectedItems = function(instanceName) {
            var selectedNodes, selectedItems;

            if (instanceName === ItemObject.name) {
                return model.itemsModel.getSelected();
            }
            if (instanceName === TreeNodeModel.name) {
                return model.treeModel.getSelected();
            }
            if (!instanceName) {
                selectedNodes = model.treeModel.getSelected();
                selectedItems = model.itemsModel.getSelected();

            	return (selectedItems.length > 0) ? selectedItems : selectedNodes;
			}
            throw new Error('Unknown item type.');
        };

        // fetch resource objects out of the selected items
        this.fetchSelectedObjects = function(item) {
            var objects = [];
            $.each(model.fetchSelectedItems(item.constructor.name), function(i, itemObject) {
                objects.push(itemObject.rdo);
            });
            return objects;
        };

        // check whether view item can be opened based on the event and configuration options
        function isItemOpenable(event) {
            // selecting with Ctrl key
            if(config.manager.selection.enabled && config.manager.selection.useCtrlKey && event.ctrlKey === true) {
                return false;
            }

            // single clicked while expected dblclick
            if(config.manager.dblClickOpen && event.type === 'click') {
                return false;
            }

            return true;
        }

        /**
         * PanelLoader Interface
		 *
         * @constructor
         */
        var PanelLoader = function() {
            this.beforeLoad = function(path) {};
            this.afterLoad = function(path, response) {};
		};

        /**
		 * Preview model
		 *
         * @constructor
         */
		var PreviewModel = function() {
			var preview_model = this,
				clipboard = null;

            this.rdo = ko.observable({});
            // computed resource data object
            this.cdo = ko.observable({});

			this.viewer = {
                type: ko.observable('default'),
                isEditable: ko.observable(false),
                url: ko.observable(null),
                pureUrl: ko.observable(null),
                options: ko.observable({}),
                content: ko.observable(null),
                codeMirror: ko.observable(null)
			};

            this.renderer = new RenderModel();
            this.editor = new EditorModel();

            this.rdo.subscribe(function (resourceObject) {
                preview_model.cdo({
                    isFolder: (resourceObject.type === 'folder'),
                    sizeFormatted: formatBytes(resourceObject.attributes.size),
                    createdFormatted: formatTimestamp(resourceObject.attributes.created),
                    modifiedFormatted: formatTimestamp(resourceObject.attributes.modified),
                    extension: (resourceObject.type === 'file') ? getExtension(resourceObject.id) : null,
                    dimensions: resourceObject.attributes.width ? resourceObject.attributes.width + 'x' + resourceObject.attributes.height : null
                });
            });

            this.editor.content.subscribe(function (content) {
            	if (preview_model.editor.isInteractive()) {
            		// instantly render changes of editor content
                    preview_model.renderer.render(content);
				}
            });

            this.applyObject = function(resourceObject) {
            	if (clipboard) {
                    clipboard.destroy();
				}

                model.previewFile(false);

                var filename = resourceObject.attributes.name,
                    editorObject = {
                        interactive: false
                    },
                    viewerObject = {
                        type: 'default',
                        url: null,
                        options: {}
                    };

                preview_model.rdo(resourceObject);

                if(isImageFile(filename)) {
                    viewerObject.type = 'image';
                    viewerObject.url = createImageUrl(resourceObject, false, true);
                }
                if(isAudioFile(filename) && config.viewer.audio.enabled === true) {
                    viewerObject.type = 'audio';
                    viewerObject.url = createPreviewUrl(resourceObject, true);
                }
                if(isVideoFile(filename) && config.viewer.video.enabled === true) {
                    viewerObject.type = 'video';
                    viewerObject.url = createPreviewUrl(resourceObject, true);
                    viewerObject.options = {
                        width: config.viewer.video.playerWidth,
                        height: config.viewer.video.playerHeight
                    };
                }
                // if(isOnlyOfficeFile(filename) && config.viewer.onlyoffice.enabled === true) {
                //     viewerObject.type = 'onlyoffice';
                //     var connectorUrl = config.viewer.onlyoffice.connectorUrl || fm.settings.baseUrl + '/connectors/php/onlyoffice/editor.php';
                //     viewerObject.url = connectorUrl + '?path=' + encodeURIComponent(resourceObject.attributes.path);
                //     viewerObject.options = {
                //         width: config.viewer.onlyoffice.editorWidth,
                //         height: config.viewer.onlyoffice.editorHeight
                //     };
                // }
                if(isOpenDocFile(filename) && config.viewer.opendoc.enabled === true) {
                    viewerObject.type = 'opendoc';
                    viewerObject.url = fm.settings.baseUrl + '/libs/ViewerJS/index.html#' + createPreviewUrl(resourceObject, true);
                    viewerObject.options = {
                        width: config.viewer.opendoc.readerWidth,
                        height: config.viewer.opendoc.readerHeight
                    };
                }
                if(isGoogleDocsFile(filename) && config.viewer.google.enabled === true) {
                    viewerObject.type = 'google';
                    viewerObject.url = 'https://docs.google.com/viewer?url=' + encodeURIComponent(createPreviewUrl(resourceObject, false)) + '&embedded=true';
                    viewerObject.options = {
                        width: config.viewer.google.readerWidth,
                        height: config.viewer.google.readerHeight
                    };
                }
                if (isIFrameFile(filename) && config.viewer.iframe.enabled === true) {
                    viewerObject.type = 'iframe';
                    viewerObject.url = createPreviewUrl(resourceObject, true);
                    viewerObject.options = {
                        width: config.viewer.iframe.readerWidth,
                        height: config.viewer.iframe.readerHeight
                    };
				}
                if ((isCodeMirrorFile(filename) && config.viewer.codeMirrorRenderer.enabled === true) ||
                    (isMarkdownFile(filename) && config.viewer.markdownRenderer.enabled === true)
				) {
                    viewerObject.type = 'renderer';
                    viewerObject.options = {
                        is_writable: resourceObject.attributes.writable
                    };
                    preview_model.renderer.setRenderer(resourceObject);
                    editorObject.interactive = preview_model.renderer.renderer().interactive;
				}

                preview_model.viewer.type(viewerObject.type);
                preview_model.viewer.url(viewerObject.url);
                preview_model.viewer.options(viewerObject.options);
                preview_model.viewer.pureUrl(createCopyUrl(resourceObject));
                preview_model.viewer.isEditable(isEditableFile(filename) && config.editor.enabled === true);
                preview_model.editor.isInteractive(editorObject.interactive);

                if (viewerObject.type === 'renderer' || preview_model.viewer.isEditable()) {
                    previewItem(resourceObject).then(function(content) {
						preview_model.viewer.content(content);
						model.previewFile(true);
                    });
				} else {
                    model.previewFile(true);
				}
            };

            this.afterRender = function() {
                preview_model.renderer.render(preview_model.viewer.content());

                var copyBtnEl = $previewWrapper.find('.btn-copy-url')[0];
                clipboard = new Clipboard(copyBtnEl);

                clipboard.on('success', function(e) {
                    fm.success(lg('copied'));
                });
            };

            this.initiateEditor = function(elements) {
            	var textarea = $previewWrapper.find('.fm-cm-editor-content')[0];
                preview_model.editor.createInstance(preview_model.cdo().extension, textarea, {
                    readOnly: false,
                    styleActiveLine: true
                });
			};

			// fires specific action by clicking toolbar buttons in detail view
			this.bindToolbar = function(action) {
				if (isObjectCapable(preview_model.rdo(), action)) {
					performAction(action, {}, preview_model.rdo());
				}
			};

            this.previewIconClass = ko.pureComputed(function() {
                var cssClass = [],
                    extraClass = ['ico'];
                if(preview_model.viewer.type() === 'default' || !preview_model.viewer.url()) {
                    cssClass.push('grid-icon');
                    if(this.cdo().isFolder === true) {
                        cssClass.push('ico_folder');
                        extraClass.push('folder');
                        if(!this.rdo().attributes.readable) {
                            extraClass.push('lock');
                        }
                    } else {
                        cssClass.push('ico_file');
                        if(this.rdo().attributes.readable) {
                            extraClass.push('ext', this.cdo().extension);
                        } else {
                            extraClass.push('file', 'lock');
                        }
                    }
                    cssClass.push(extraClass.join('_'));
                }
                return cssClass.join(' ');
            }, this);

            this.closePreview = function() {
                model.previewFile(false);
            };

			this.editFile = function() {
				var content = preview_model.viewer.content();
                preview_model.renderer.render(content);
                preview_model.editor.render(content);
			};

			this.saveFile = function() {
				saveItem(preview_model.rdo());
			};

			this.closeEditor = function() {
                preview_model.editor.enabled(false);
                // re-render viewer content
                preview_model.renderer.render(preview_model.viewer.content());
			};

			this.buttonVisibility = function(action) {
				switch(action) {
					case 'select':
						return (isObjectCapable(preview_model.rdo(), action) && hasContext());
					case 'move':
					case 'rename':
					case 'delete':
					case 'download':
						return (isObjectCapable(preview_model.rdo(), action));
				}
			};
		};

		var TreeModel = function() {
			var tree_model = this;
			this.selectedNode = ko.observable(null);

            var rootNode = new TreeNodeModel({attributes: {}});
            rootNode.id = fileRoot;
            rootNode.level = ko.observable(-1);
            this.rootNode = rootNode;

			function expandFolderDefault(parentNode) {
				if (fullexpandedFolder !== null) {
					if(!parentNode) {
						parentNode = tree_model.rootNode;
					}

					// looking for node that starts with specified path
					var node = tree_model.findByFilter(function (node) {
						return (fullexpandedFolder.indexOf(node.id) === 0);
					}, parentNode);

					if (node) {
                        config.filetree.expandSpeed = 10;
                        tree_model.loadDataNode(node, false, true);
					} else {
						fullexpandedFolder = null;
                        config.filetree.expandSpeed = 200;
                        tree_model.setItemsFromNode(parentNode);
					}
				}
			}

            this.mapNodes = function(filter, contextNode) {
                if (!contextNode) {
                    contextNode = tree_model.rootNode;
                }
                // don't apply callback function to the filetree root node
                if (!contextNode.isRoot()) {
                    filter.call(this, contextNode);
                }
                var nodes = contextNode.children();
                if (!nodes || nodes.length === 0) {
                    return null;
                }
                for (var i = 0, l = nodes.length; i < l; i++) {
                    filter.call(this, nodes[i]);
                    tree_model.findByFilter(filter, nodes[i]);
                }
            };

			this.findByParam = function(key, value, contextNode) {
				if(!contextNode) {
					contextNode = tree_model.rootNode;
					if(contextNode[key] === value) {
						return contextNode;
					}
				}
				var nodes = contextNode.children();
				if(!nodes || nodes.length === 0) {
					return null;
				}
				for (var i = 0, l = nodes.length; i < l; i++) {
					if (nodes[i][key] === value) {
						return nodes[i];
					}
					var result = tree_model.findByParam(key, value, nodes[i]);
					if(result) return result;
				}
				return null;
			};

			this.findByFilter = function(filter, contextNode) {
				if(!contextNode) {
					contextNode = tree_model.rootNode;
					if(filter(contextNode)) {
						return contextNode;
					}
				}
				var nodes = contextNode.children();
				if(!nodes || nodes.length === 0) {
					return null;
				}
				for (var i = 0, l = nodes.length; i < l; i++) {
					if(filter(nodes[i])) {
						return nodes[i];
					}
					var result = tree_model.findByFilter(filter, nodes[i]);
					if(result) return result;
				}
				return null;
			};

            this.getSelected = function() {
                var selectedItems = [];
                if (tree_model.selectedNode()) {
                    selectedItems.push(tree_model.selectedNode());
				}
                return selectedItems;
            };

			this.loadDataNode = function(targetNode, populateItems, refresh) {
                var targetPath = targetNode.id;
                var folderLoader = new FolderAjaxLoader(targetPath);

                folderLoader
                    .setPreloader(tree_model.getPreloader(targetNode))
                    .setDataHandler(function (resourceObjects, targetPath) {
                        tree_model.addNodes(resourceObjects, targetNode, refresh);
                    });

                if (populateItems) {
                    folderLoader
                        .setPreloader(model.itemsModel.getPreloader())
                        .setDataHandler(function (resourceObjects, targetPath) {
                            model.itemsModel.addItems(resourceObjects, targetPath, refresh);
                            model.searchModel.clearInput();
                        });
                }

                folderLoader.load(function() {
                    return readFolder(targetPath);
                });
			};

            this.getPreloader = function(targetNode) {
                var preloader = function() {};
                preloader.prototype = Object.create(PanelLoader);

                preloader.prototype.beforeLoad = function(path) {
                    if(!targetNode.isRoot()) {
                        targetNode.isLoaded(false);
                    }
                };

                preloader.prototype.afterLoad = function(path, response) {
                    if(!targetNode.isRoot()) {
                        targetNode.isLoaded(true);
                        tree_model.expandNode(targetNode);
                    }
                    expandFolderDefault(targetNode);
                };

                return new preloader();
			};

			this.createNode = function(resourceObject) {
				var node = new TreeNodeModel(resourceObject);
                fmModel.filterModel.filterItem(node);
				return node;
			};

            this.createNodes = function(resourceObjects) {
                var nodes = [];
                $.each(resourceObjects, function(i, resourceObject) {
                    nodes.push(tree_model.createNode(resourceObject));
                });
                return nodes;
            };

			this.appendNodes = function(targetNode, newNodes) {
				if(!$.isArray(newNodes)) {
					newNodes = [newNodes];
				}
				if (!targetNode) {
					targetNode = tree_model.rootNode;
				}
				// list only folders in tree
				if(config.filetree.foldersOnly) {
					newNodes = $.grep(newNodes, function(node) {
						return (node.cdo.isFolder);
					});
				}
				$.each(newNodes, function(i, node) {
					node.parentNode(targetNode);
				});
				var allNodes = targetNode.children().concat(newNodes);
				targetNode.children(sortItems(allNodes));
			};

            this.addNodes = function(resourceObjects, targetNode, reset) {
                if(!$.isArray(resourceObjects)) {
                    resourceObjects = [resourceObjects];
                }

                if(targetNode) {
                    var newNodes = tree_model.createNodes(resourceObjects);
                    if (reset) {
                        targetNode.children([]);
                    }
                    tree_model.appendNodes(targetNode, newNodes);
                }
			};

			this.expandNode = function(node) {
				if(node.isExpanded() === false && node.isLoaded() === true) {
					node.isSliding(true);
					return true;
				}
				return false;
			};

			this.collapseNode = function(node) {
				if(node.isExpanded() === true) {
					node.isSliding(true);
					return true;
				}
				return false;
			};

            this.toggleNode = function(node) {
				if(!tree_model.collapseNode(node)) {
                    tree_model.expandNode(node);
				}
            };

			this.arrangeNode = function(node) {
				var childrenLength = node.children().length;
				$.each(node.children(), function(index, cNode) {
					cNode.level(node.level() + 1);
					cNode.isFirstNode(index === 0);
					cNode.isLastNode(index === (childrenLength - 1));
				});
			};

            this.setItemsFromNode = function(node) {
                var dataObjects = [];
                $.each(node.children(), function(i, cnode) {
                    dataObjects.push(cnode.rdo);
                });
                model.itemsModel.addItems(dataObjects, node.id, true);
            };

			this.nodeRendered = function(elements, node) {
				// attach context menu
				$(elements[1]).contextMenu({
					selector: '.file, .directory',
					zIndex: 100,
					// wrap options with "build" allows to get item element
					build: function ($triggerElement, e) {
                        node.selected(true);

						return {
							appendTo: '.fm-container',
							items: getContextMenuItems(node.rdo),
							callback: function(itemKey, options) {
								performAction(itemKey, options, node.rdo, model.fetchSelectedObjects(node));
							}
						}
					}
				});
			};

			this.actualizeNodeObject = function(node, oldFolder, newFolder) {
				var search = new RegExp('^' + oldFolder);
				var oldPath = node.rdo.id;
				var newPath = oldPath.replace(search, newFolder);
				node.id = newPath;
				node.rdo.id = newPath;
				node.rdo.attributes.path = node.rdo.attributes.path.replace(new RegExp(oldPath + '$'), newPath);

				if(node.children().length) {
					$.each(node.children(), function(index, cNode) {
						tree_model.actualizeNodeObject(cNode, oldFolder, newFolder);
					});
				}
			};
		};

        var TreeNodeModel = function(resourceObject) {
            var tree_node = this;
            this.id = resourceObject.id;
            this.rdo = resourceObject;
            this.cdo = { // computed data object
                isFolder: (resourceObject.type === 'folder'),
                extension: (resourceObject.type === 'file') ? getExtension(resourceObject.id) : null,
                dimensions: resourceObject.attributes.width ? resourceObject.attributes.width + 'x' + resourceObject.attributes.height : null,
                cssItemClass: (resourceObject.type === 'folder') ? 'directory' : 'file',
                hiddenByType: false,
                hiddenBySearch: false
            };

            this.visible = ko.observable(true);
            this.nodeTitle = ko.observable(resourceObject.attributes.name);
            this.children = ko.observableArray([]);
            this.parentNode = ko.observable(null);
            this.isSliding = ko.observable(false);
            this.isLoading = ko.observable(false);
            this.isLoaded = ko.observable(false);
            this.isExpanded = ko.observable(false);
            this.selected = ko.observable(false);
            this.dragHovered = ko.observable(false);
            // arrangable properties
            this.level = ko.observable(0);
            this.isFirstNode = ko.observable(false);
            this.isLastNode = ko.observable(false);

            this.nodeTitle.subscribe(function (value) {
                tree_node.rdo.attributes.name = value;
            });

            this.children.subscribe(function (value) {
                model.treeModel.arrangeNode(tree_node);
            });

            this.isLoaded.subscribe(function (value) {
                tree_node.isLoading(!value);
            });

            this.selected.subscribe(function (value) {
                if (value) {
                    if (model.treeModel.selectedNode() !== null) {
                        model.treeModel.selectedNode().selected(false);
                    }
                    model.treeModel.selectedNode(tree_node);
                    model.itemsModel.unselectItems();
                } else {
                    model.treeModel.selectedNode(null);
                }
            });

            this.switchNode = function(node) {
                if(!node.cdo.isFolder) {
                    return false;
                }
                if(!node.rdo.attributes.readable) {
                    fm.error(lg('NOT_ALLOWED_SYSTEM'));
                    return false;
                }
                if(!node.isLoaded()) {
                    tree_node.openNode(node, false);
                } else {
                    model.treeModel.toggleNode(node);
                }
            };

            this.mouseDown = function(node, e) {
                node.selected(true);
            };

            this.nodeClick = function(node, e) {
                if(!config.manager.dblClickOpen) {
                    tree_node.openNode(node, true);
                }
            };

            this.nodeDblClick = function(node, e) {
                if(config.manager.dblClickOpen) {
                    tree_node.openNode(node, true);
                }
            };

            this.openNode = function(node, populateItems, e) {
                if(node.rdo.type === 'file') {
                    getDetailView(node.rdo);
                }
                if(node.rdo.type === 'folder') {
                    if(!node.isLoaded() || (node.isExpanded() && config.filetree.reloadOnClick)) {
                        model.treeModel.loadDataNode(node, populateItems, true);
                    } else {
                        model.treeModel.toggleNode(node);

                        if (populateItems) {
                            model.treeModel.setItemsFromNode(node);
                        }
                    }
                }
            };

            this.remove = function() {
                tree_node.parentNode().children.remove(tree_node);
            };

            this.isRoot = function() {
                return tree_node.level() === model.treeModel.rootNode.level();
            };

            this.title = ko.pureComputed(function() {
                return (config.options.showTitleAttr) ? this.rdo.id : null;
            }, this);

            this.itemClass = ko.pureComputed(function() {
                var cssClass = [];
                if (this.selected() && config.manager.selection.enabled) {
                    cssClass.push('ui-selected');
                }
                if (this.dragHovered()) {
                    cssClass.push(model.ddModel.hoveredCssClass);
                }
                return cssClass.join(' ');
            }, this);

            this.iconClass = ko.pureComputed(function() {
                var cssClass,
                    extraClass = ['ico'];
                if(this.cdo.isFolder === true) {
                    cssClass = 'ico_folder';
                    if(this.isLoading() === true) {
                        extraClass.push('loading');
                    } else {
                        extraClass.push('folder');
                        if(!this.rdo.attributes.readable) {
                            extraClass.push('lock');
                        } else if(this.isExpanded() || !this.isExpanded() && this.isSliding()) {
                            extraClass.push('open');
                        }
                    }
                } else {
                    cssClass = 'ico_file';
                    if(this.rdo.attributes.readable) {
                        extraClass.push('ext', this.cdo.extension);
                    } else {
                        extraClass.push('file', 'lock');
                    }
                }
                return cssClass + ' ' + extraClass.join('_');
            }, this);

            this.switcherClass = ko.pureComputed(function() {
                var cssClass = [];
                if (config.filetree.showLine) {
                    if (this.level() === 0 && this.isFirstNode() && this.isLastNode()) {
                        cssClass.push('root');
                    } else if (this.level() === 0 && this.isFirstNode()) {
                        cssClass.push('roots');
                    } else if (this.isLastNode()) {
                        cssClass.push('bottom');
                    } else {
                        cssClass.push('center');
                    }
                } else {
                    cssClass.push('noline');
                }
                if (this.cdo.isFolder) {
                    var isOpen = (this.isExpanded() || !this.isExpanded() && this.isSliding());
                    cssClass.push(isOpen ? 'open' : 'close');
                } else {
                    cssClass.push('docu');
                }
                return cssClass.join('_');
            }, this);

            this.clusterClass = ko.pureComputed(function() {
                return (config.filetree.showLine && !this.isLastNode()) ? 'line' : '';
            }, this);
        };

		var ItemsModel = function() {
			var items_model = this;
			this.objects = ko.observableArray([]);
            this.parentItem = ko.observable(null);
			this.objectsSize = ko.observable(0);
			this.objectsNumber = ko.observable(0);
			this.selectedNumber = ko.observable(0);
			this.listSortField = ko.observable(configSortField);
			this.listSortOrder = ko.observable(configSortOrder);
			this.isSelecting = ko.observable(false);
			this.continiousSelection = ko.observable(false);
            this.descriptivePanel = new RenderModel();
            this.lazyLoad = null;

			this.isSelecting.subscribe(function(state) {
				if(!state) {
				    // means selection lasso has been dropped
                    items_model.continiousSelection(false);
				}
			});

            this.createItem = function(resourceObject) {
                var item = new ItemObject(resourceObject);
                fmModel.filterModel.filterItem(item);
                return item;
            };

            this.createItems = function(resourceObjects) {
                var items = [];
                $.each(resourceObjects, function(i, resourceObject) {
                    items.push(items_model.createItem(resourceObject));
                });
                return items;
            };

            this.appendItems = function(items) {
                if(!$.isArray(items)) {
                    items = [items];
                }

                var allItems = items_model.objects().concat(items);
                items_model.objects(sortItems(allItems));
            };

            this.addItems = function(resourceObjects, targetPath, reset) {
                if(!$.isArray(resourceObjects)) {
                    resourceObjects = [resourceObjects];
                }

                var items = items_model.createItems(resourceObjects);

                if (reset) {
                    model.currentPath(targetPath);
                    model.breadcrumbsModel.splitCurrent();

                    items_model.setDescriptivePanel(resourceObjects);
                    items_model.setItemsList(items);
                    items_model.addParentItem();
                } else {
                    items_model.appendItems(items);
                }
            };

			this.loadDataList = function(targetPath) {
                var folderLoader = new FolderAjaxLoader(targetPath);

                folderLoader
                    .setPreloader(items_model.getPreloader())
                    .setDataHandler(function (resourceObjects, targetPath) {
                        items_model.addItems(resourceObjects, targetPath, true);
                        model.searchModel.clearInput();
                    })
                    .load(function() {
                        return readFolder(targetPath);
                    });
			};

			this.setItemsList = function(items) {
			    // clear parent item
                items_model.parentItem(null);

                items = sortItems(items);
                items_model.objects(items);
			};

            this.addParentItem = function() {
                // parent item is displayed for non-root folders
                if(isFile(model.currentPath()) || model.currentPath() === fileRoot) {
                    return;
                }

                var parentPath = getParentDirname(model.currentPath());
                var parentItem = {
                    id: parentPath,
                    rdo: {
                        id: parentPath,
                        type: 'parent',
                        attributes: {
                            readable: true,
                            writable: true
                        }
                    },
                    dragHovered: ko.observable(false)
                };

                parentItem.open = function(item, e) {
                    if(isItemOpenable(e)) {
                        items_model.loadDataList(parentItem.id);
                    }
                };

                parentItem.itemClass = ko.pureComputed(function() {
                    var cssClass = [];
                    if (parentItem.dragHovered()) {
                        cssClass.push(model.ddModel.hoveredCssClass);
                    }
                    return cssClass.join(' ');
                });

                items_model.parentItem(parentItem);
            };

            this.setDescriptivePanel = function(dataObjects) {
                // clear previously rendered content
                items_model.descriptivePanel.content(null);

                $.each(dataObjects, function (i, resourceObject) {
                    if (config.manager.renderer.position && typeof config.manager.renderer.indexFile === 'string' &&
                        resourceObject.attributes.name.toLowerCase() === config.manager.renderer.indexFile.toLowerCase()
                    ) {
                        items_model.descriptivePanel.setRenderer(resourceObject);
                        // load and render index file content
                        previewItem(items_model.descriptivePanel.rdo()).then(function(content) {
							items_model.descriptivePanel.render(content);
                        });
                    }
                });
            };

			this.findByParam = function(key, value) {
				return ko.utils.arrayFirst(items_model.objects(), function(object) {
					return object[key] === value;
				});
			};

			this.findByFilter = function(filter, allMatches) {
				var firstMatch = !(allMatches || false);

				var resultItems = [],
					items = items_model.objects();

				if(!items || items.length === 0) {
                    return firstMatch ? null : resultItems;
				}
				for (var i = 0, l = items.length; i < l; i++) {
					if(filter(items[i])) {
						if(firstMatch) {
							return items[i];
						}
						resultItems.push(items[i]);
					}
				}
				return firstMatch ? null : resultItems;
			};

			this.sortObjects = function() {
				var sortedList = sortItems(items_model.objects());
				items_model.objects(sortedList);
			};

			this.getSelected = function() {
				var selectedItems = items_model.findByFilter(function (item) {
					return item.selected();
				}, true) || [];

				items_model.selectedNumber(selectedItems.length);
				return selectedItems;
			};

			this.unselectItems = function(ctrlKey) {
				var appendSelection = (config.manager.selection.enabled && config.manager.selection.useCtrlKey && ctrlKey === true);
				if(!appendSelection) {
					// drop selection from selected items
					$.each(items_model.getSelected(), function(i, itemObject) {
						itemObject.selected(false);
					});
				}
			};

            this.initiateLazyLoad = function() {
            	// not configured or already initiated
                if (config.viewer.image.lazyLoad !== true || items_model.lazyLoad) {
                	return;
				}

                items_model.lazyLoad = new LazyLoad({
                    container: $fileinfo[0], // work only for default scrollbar
                    callback_load: function (element) {
                        fm.console('LOADED', element.getAttribute('data-original'));
                    },
                    callback_set: function (element) {
                        fm.console('SET', element.getAttribute('data-original'));
                    },
                    callback_processed: function (elementsLeft) {
                        fm.console('PROCESSED', elementsLeft + ' images left');
                    }
                });
            };

            this.getPreloader = function() {
                var preloader = function() {};
                preloader.prototype = Object.create(PanelLoader);

                preloader.prototype.beforeLoad = function(path) {
                    model.loadingView(true);
                };

                preloader.prototype.afterLoad = function(path, response) {
                    model.loadingView(false);

                    if (items_model.lazyLoad) {
                        items_model.lazyLoad.update();
                    }
                };

                return new preloader();
            };

			this.objects.subscribe(function(items) {
				var totalNumber = 0,
					totalSize = 0;

				$.each(items, function(i, item) {
					totalNumber++;
					if(item.rdo.type === 'file') {
						totalSize += Number(item.rdo.attributes.size);
					}
				});
				// updates folder summary info
				items_model.objectsNumber(totalNumber);
				items_model.objectsSize(formatBytes(totalSize));

				// update
                if (items_model.lazyLoad) {
                    setTimeout(function() {
                        items_model.lazyLoad.update();
                    }, 50);
                }

                // context menu
				$viewItems.contextMenu({
					selector: '.file, .directory',
					zIndex: 100,
					// wrap options with "build" allows to get item element
					build: function ($triggerElement, e) {
						var koItem = ko.dataFor($triggerElement[0]);
						if(!koItem.selected()) {
							model.itemsModel.unselectItems(false);
							koItem.selected(true);
						}

						return {
							appendTo: '.fm-container',
							items: getContextMenuItems(koItem.rdo),
							callback: function(itemKey, options) {
								performAction(itemKey, options, koItem.rdo, model.fetchSelectedObjects(koItem));
							}
						}
					}
				});
			});
		};

        var ItemObject = function(resourceObject) {
            var item_object = this,
				previewWidth = config.viewer.image.thumbMaxWidth;

            if(resourceObject.attributes.width && resourceObject.attributes.width < previewWidth) {
                previewWidth = resourceObject.attributes.width;
            }

            this.id = resourceObject.id; // for search purpose
            this.rdo = resourceObject; // original resource data object
            this.cdo = { // computed data object
                isFolder: (resourceObject.type === 'folder'),
                sizeFormatted: formatBytes(resourceObject.attributes.size),
                createdFormatted: formatTimestamp(resourceObject.attributes.created),
                modifiedFormatted: formatTimestamp(resourceObject.attributes.modified),
                extension: (resourceObject.type === 'file') ? getExtension(resourceObject.id) : null,
                dimensions: resourceObject.attributes.width ? resourceObject.attributes.width + 'x' + resourceObject.attributes.height : null,
                cssItemClass: (resourceObject.type === 'folder') ? 'directory' : 'file',
                imageUrl: createImageUrl(resourceObject, true, true),
                previewWidth: previewWidth,
                hiddenByType: false,
            	hiddenBySearch: false
            };
            this.visible = ko.observable(true);
            this.selected = ko.observable(false);
            this.dragHovered = ko.observable(false);
            this.lazyPreview = (config.viewer.image.lazyLoad && this.cdo.imageUrl);

            this.selected.subscribe(function (value) {
                if (value && model.treeModel.selectedNode() !== null) {
                    model.treeModel.selectedNode().selected(false);
                }
            });

            this.title = ko.pureComputed(function() {
                return (config.options.showTitleAttr) ? this.rdo.id : null;
            }, this);

            this.itemClass = ko.pureComputed(function() {
                var cssClass = [];
                if (this.selected() && config.manager.selection.enabled) {
                    cssClass.push('ui-selected');
                }
                if (this.dragHovered()) {
                    cssClass.push(model.ddModel.hoveredCssClass);
                }
                return this.cdo.cssItemClass + ' ' + cssClass.join(' ');
            }, this);

            this.listIconClass = ko.pureComputed(function() {
                var cssClass,
                    extraClass = ['ico'];
                if (this.cdo.isFolder === true) {
                    cssClass = 'ico_folder';
                    extraClass.push('folder');
                    if (!this.rdo.attributes.readable) {
                        extraClass.push('lock');
                    }
                } else {
                    cssClass = 'ico_file';
                    if (this.rdo.attributes.readable) {
                        extraClass.push('ext', this.cdo.extension);
                    } else {
                        extraClass.push('file', 'lock');
                    }
                }
                return cssClass + ' ' + extraClass.join('_');
            }, this);

            this.gridIconClass = ko.pureComputed(function() {
                var cssClass = [],
                    extraClass = ['ico'];
                if (!this.cdo.imageUrl) {
                    cssClass.push('grid-icon');
                    if (this.cdo.isFolder === true) {
                        cssClass.push('ico_folder');
                        extraClass.push('folder');
                        if (!this.rdo.attributes.readable) {
                            extraClass.push('lock');
                        }
                    } else {
                        cssClass.push('ico_file');
                        if (this.rdo.attributes.readable) {
                            extraClass.push('ext', this.cdo.extension);
                        } else {
                            extraClass.push('file', 'lock');
                        }
                    }
                    cssClass.push(extraClass.join('_'));
                }
                return cssClass.join(' ');
            }, this);

            this.mouseDown = function(item, e) {
                // case: previously selected items are dragged instead of a newly one
                // unselect if currently clicked item is not the one of selected items
                if (!item.selected()) {
                    model.itemsModel.unselectItems(e.ctrlKey);
                }

                model.selectionModel.unselect = item.selected();
                item.selected(true);
            };

            this.open = function(item, e) {
                if (model.selectionModel.unselect) {
                    // case: click + ctrlKey on selected item
                    if (e.ctrlKey) {
                        item.selected(false);
                    }
                    // drop selection
                    if (!e.ctrlKey && config.manager.dblClickOpen) {
                        model.itemsModel.unselectItems(e.ctrlKey);
                        item.selected(true);
                    }
                }

                if(isItemOpenable(e)) {
                    if(config.options.quickSelect && item.rdo.type === 'file' && isObjectCapable(item.rdo, 'select')) {
                        selectItem(item.rdo);
                    } else {
                        getDetailView(item.rdo);
                    }
                }
            };

            this.remove = function() {
                model.itemsModel.objects.remove(this);
            };
        };

        var FolderAjaxLoader = function(path) {
			var folder_loader = this,
				handlers = [],
                /** @type {PanelLoader[]} */
				preloaders = [];

            /**
             * @param {PanelLoader} preloader
             * @returns {FolderAjaxLoader}
             */
            this.setPreloader = function(preloader) {
                preloaders.push(preloader);
                return folder_loader;
            };

            /**
			 *
             * @param {function} callback
             * @returns {FolderAjaxLoader}
             */
            this.setDataHandler = function(callback) {
                handlers.push(callback);
                return folder_loader;
            };

            this.load = function(folderLoader) {
                preloaders.forEach(function (preloader, index, array) {
                    preloader.beforeLoad(path);
				});

                folderLoader().then(function(response) {
                    if(response.data) {
                        $.each(handlers, function(i, handler) {
                            handler(response.data, path);
                        });

                        $.each(preloaders, function(i, preloader) {
                            preloader.afterLoad(path, response);
                        });
                    }
                });
            };
		};

		var TableViewModel = function() {
			var SortableHeader = function(name) {
				var thead = this;
				this.column = ko.observable(name);
				this.order = ko.observable(model.itemsModel.listSortOrder());

				this.sortClass = ko.pureComputed(function() {
					var cssClass;
					if(model.itemsModel.listSortField() === thead.column()) {
						cssClass = 'sorted sorted-' + this.order();
					}
					return cssClass;
				}, this);

				this.sort = function() {
					var isAscending = thead.order() === 'asc';
					var isSameColumn = model.itemsModel.listSortField() === thead.column();
					thead.order(isSameColumn ? (isAscending ? 'desc' : 'asc') : model.itemsModel.listSortOrder());
					model.itemsModel.listSortField(thead.column());
					model.itemsModel.listSortOrder(thead.order());
					model.itemsModel.sortObjects();
				};
			};

			this.thName = new SortableHeader('name');
			this.thType = new SortableHeader('type');
			this.thSize = new SortableHeader('size');
			this.thDimensions = new SortableHeader('dimensions');
			this.thModified = new SortableHeader('modified');
		};

		var HeaderModel = function() {
            var header_model = this;

			this.closeButton = ko.observable(false);
			this.langSwitcher = $.isArray(config.language.available) && config.language.available.length > 0;

            this.closeButtonOnClick = function() {
                fm.console('CLOSE button is clicked');
			};

			this.navHome = function() {
				model.previewFile(false);
				model.itemsModel.loadDataList(fileRoot);
			};

			this.navLevelUp = function() {
				var parentFolder = model.previewFile()
                    ? getDirname(model.previewModel.rdo().id)
                    : getParentDirname(model.currentPath());

				if(model.previewFile()) {
					model.previewFile(false);
				}

                if(parentFolder !== model.currentPath()) {
					model.itemsModel.loadDataList(parentFolder);
                }
			};

            this.navRefresh = function() {
                if(model.previewFile()) {
                    model.previewFile(false);
                    model.previewFile(true);
                } else {
                    model.itemsModel.loadDataList(model.currentPath());
				}
            };

			this.displayGrid = function() {
				model.viewMode('grid');
				model.previewFile(false);

                if (model.itemsModel.lazyLoad) {
                    model.itemsModel.lazyLoad.update();
                }
			};

			this.displayList = function() {
				model.viewMode('list');
				model.previewFile(false);
			};

            this.switchLang = function(e) {
                var langNew = e.target.value,
                    langCurrent = langModel.getLang();

				if (langNew && langNew.toLowerCase() !== langCurrent.toLowerCase()) {
					var newUrl, url = window.location.toString(),
						regExp = new RegExp('(langCode=)' + langCurrent);

					if (regExp.test(url)) {
                        newUrl = url.replace(regExp, '$1' + langNew);
					} else {
                        newUrl = url + ($.isEmptyObject(_url_.param()) ? '?' : '#') + 'langCode=' + langNew;
					}
					window.location.href = newUrl;
				}
            };

			this.createFolder = function() {
                if(!hasCapability('createFolder')) {
                    fm.error(lg('NOT_ALLOWED'));
                    return false;
                }

				function makeFolder(e, ui) {
					var folderName = ui.getInputValue();
					if(!folderName) {
						fm.error(lg('no_foldername'));
						return;
					}

                    buildAjaxRequest('GET', {
                        mode: 'addfolder',
                        path: fmModel.currentPath(),
                        name: folderName
                    }).done(function(response) {
                        if (response.data) {
                            fmModel.addElements(response.data, fmModel.currentPath());

                            ui.closeDialog();
                            if (config.options.showConfirmation) {
                                fm.success(lg('successful_added_folder'));
                            }
                        }
                    }).fail(handleAjaxError);
				}

				fm.prompt({
					message: lg('prompt_foldername'),
					value: lg('default_foldername'),
					okBtn: {
						label: lg('create_folder'),
						autoClose: false,
						click: makeFolder
					},
					cancelBtn: {
						label: lg('cancel')
					}
				});
			};
		};

		var SummaryModel = function() {
			this.files = ko.observable(null);
			this.folders = ko.observable(null);
			this.size = ko.observable(null);
			this.enabled = ko.observable(false);

			this.doSummarize = function() {
				summarizeItems();
			};
		};

        var FilterModel = function() {
            var filter_model = this;
            this.name = ko.observable(null);

            this.setName = function(filterName) {
                if (filterName &&
                    config.filter &&
                    $.isArray(config.filter[filterName])
                ) {
                    filter_model.name(filterName);
				}
            };

            // return extensions which are match a filter name
            this.getExtensions = function() {
            	if (filter_model.name()) {
            		return config.filter[filter_model.name()];
				}
				return null;
            };

            // check whether file item should be filtered out of the output based on it's extension
            this.filterItem = function(itemObject) {
                var extensions = filter_model.getExtensions(),
                	visibility = !itemObject.cdo.hiddenBySearch;

            	// set default visibility, required for "all files" filter
                itemObject.cdo.hiddenByType = false;

                if (itemObject.rdo.type === 'file' && $.isArray(extensions)) {
                    var ext = getExtension(itemObject.id),
                        matchByType = extensions.indexOf(ext) !== -1;

                    visibility = visibility && matchByType;
                    itemObject.cdo.hiddenByType = !matchByType;
				}
                itemObject.visible(visibility);
            };

            this.filter = function(filterName) {
                filter_model.setName(filterName);

				$.each(model.itemsModel.objects(), function(i, itemObject) {
                    filter_model.filterItem(itemObject);
				});

                model.treeModel.mapNodes(function (node) {
					filter_model.filterItem(node);
                });

                if (model.itemsModel.lazyLoad) {
                    model.itemsModel.lazyLoad.update();
                }
            };

            this.reset = function() {
                filter_model.name(null);
                filter_model.filter(null);
            };
        };

        var SearchModel = function() {
            var search_model = this,
                previousValue = '',
                searchOnTyping = !!config.search.typingDelay;

            this.value = ko.observable('');
            this.isRendered = ko.observable(false);

            this.value.subscribe(function(oldValue) {
                previousValue = oldValue;
            }, null, 'beforeChange');

            this.inputKeyUp = function(data, e) {
                var keyCode = e.which || e.keyCode,
                    // https://stackoverflow.com/a/19347349/7095038
                    invalidKeyCodes = [16,17,18,27,37,38,39,40];

                if (searchOnTyping) {
                    // validate input
                    if (invalidKeyCodes.indexOf(keyCode) > -1) {
                        return;
                    }
                    // explicit assign value, required for search-on-typing
                    search_model.value(e.target.value);
				}

                // search-on-typing or Enter key pressed
                if (searchOnTyping || keyCode === 13) {
                	performSearch();
                }
            };

            this.seekItems = function(data, e) {
                performSearch();
            };

            this.reset = function (data, e) {
                restoreItems();
            };

            this.clearInput = function () {
                // reset search string
                previousValue = '';
                search_model.value('');
                search_model.isRendered(false);
                delayStack.removeTimer('search');
            };

            function performSearch() {
            	if (searchOnTyping) {
                    // create delayed timer
                    delayStack.push('search', function() {
                    	searchItems();
                    }, config.search.typingDelay);
				} else {
                    searchItems();
				}
			}

            function searchItems() {
            	var searchString = search_model.value(),
                	subject = config.search.caseSensitive ? searchString : searchString.toLowerCase();

                if (searchString === '') {
                    if (searchString !== previousValue) {
                        restoreItems();
					} else {
                        fm.warning(lg('search_string_empty'));
					}
                    return;
                }

                if (config.search.recursive) {
                    // recursive search with server-side request
                    var targetPath = model.currentPath();
                    var folderLoader = new FolderAjaxLoader(targetPath);

                    folderLoader
                        .setPreloader(model.itemsModel.getPreloader())
                        .setDataHandler(function (dataObject, targetPath) {
                            var resourceObjects = [];

                            if (config.search.caseSensitive) {
                                $.each(dataObject, function (i, resourceObject) {
                                    if (resourceObject.attributes.name.indexOf(subject) === 0) {
                                        resourceObjects.push(resourceObject);
                                    }
                                });
                            } else {
                                resourceObjects = dataObject;
                            }

                            var items = model.itemsModel.createItems(resourceObjects);
                            model.itemsModel.setItemsList(items);
                            search_model.isRendered(true);
                        })
                        .load(function () {
                            return seekFolder(targetPath, searchString);
                        });
                } else {
                    // client-side search in the currently open folder
                    $.each(model.itemsModel.objects(), function (i, itemObject) {
                        var filename = itemObject.rdo.attributes.name;
                        if (!config.search.caseSensitive) {
                            filename = filename.toLowerCase();
                        }

                        var matchByName = (filename.indexOf(subject) === 0);
                        var visibility = !itemObject.cdo.hiddenByType;
                        visibility = visibility && matchByName;

                        itemObject.cdo.hiddenBySearch = !matchByName;
                        itemObject.visible(visibility);
                    });
                    search_model.isRendered(true);
                }
            }

            function restoreItems() {
                search_model.clearInput();

                // restore original content of the current folder
                if (config.search.recursive) {
                    model.itemsModel.loadDataList(model.currentPath());
                } else {
                    $.each(model.itemsModel.objects(), function (i, itemObject) {
                        itemObject.cdo.hiddenBySearch = false;
                        itemObject.visible(!itemObject.cdo.hiddenByType);
                    });
                }
            }
        };

		var ClipboardModel = function() {
			var cbMode = null,
                cbObjects = [],
            	clipboard_model = this,
				active = hasCapability('copy') && hasCapability('move');

            this.itemsNum = ko.observable(0);
            this.enabled = ko.observable(model.config().clipboard.enabled && active);

			this.copy = function() {
				if (!clipboard_model.hasCapability('copy')) {
					return;
				}
                cbMode = 'copy';
                cbObjects = model.fetchSelectedItems();
                clipboard_model.itemsNum(cbObjects.length);
			};

			this.cut = function() {
                if (!clipboard_model.hasCapability('cut')) {
                    return;
                }
                cbMode = 'cut';
                cbObjects = model.fetchSelectedItems();
                clipboard_model.itemsNum(cbObjects.length);
			};

			this.paste = function() {
                var	targetPath = model.currentPath();

                if (!clipboard_model.hasCapability('paste') || clipboard_model.isEmpty()) {
                    return;
                }
                if (cbMode === null || cbObjects.length === 0) {
                    fm.warning(lg('clipboard_empty'));
                    return;
                }

                processMultipleActions(cbObjects, function (i, itemObject) {
                    if (cbMode === 'cut') {
                        return moveItem(itemObject, targetPath);
                    }
                    if (cbMode === 'copy') {
                        return copyItem(itemObject, targetPath);
                    }
                }, clearClipboard);
			};

			this.clear = function() {
                if (!clipboard_model.hasCapability('clear') || clipboard_model.isEmpty()) {
                    return;
                }
                clearClipboard();
                fm.success(lg('clipboard_cleared'));
			};

            this.isEmpty = function() {
            	return cbObjects.length === 0;
			};

            this.hasCapability = function(capability) {
            	if (!clipboard_model.enabled) {
            		return false;
				}

            	switch(capability) {
					case 'copy':
						return hasCapability('copy');
                    case 'cut':
                        return hasCapability('move');
					default:
                        return true;
				}
			};

			function clearClipboard() {
                cbObjects = [];
                cbMode = null;
                clipboard_model.itemsNum(0);
			}
		};

        var BreadcrumbsModel = function() {
        	var bc_model = this;

			this.items = ko.observableArray([]);

            this.clean = function() {
                bc_model.items([]);
                // push root node
                bc_model.add(fileRoot, '');
            };

            this.add = function(path, label) {
                bc_model.items.push(new BcItem(path, label));
            };

            this.splitPath = function(targetPath) {
                var	path = fileRoot,
                    chunks = targetPath.replace(new RegExp('^' + fileRoot), '').split('/');

                // reset breadcrumbs
                bc_model.clean();

                while (chunks.length > 0) {
                    var chunk = chunks.shift();
                    if (chunk) {
                        path += chunk + '/';
                        bc_model.add(path, chunk);
                    }
                }
            };

            this.splitCurrent = function() {
                bc_model.splitPath(model.currentPath());
            };

            this.getLabel = ko.pureComputed(function() {
                var label = model.searchModel.isRendered() ? lg('search_results') : lg('current_folder');
                return label + ': ';
            }, this);

            var BcItem = function(path, label) {
                var bc_item = this;
				this.path = path;
                this.label = label;
                this.isRoot = (path === fileRoot);
                this.active = (path === model.currentPath());

                this.itemClass = function() {
                    var cssClass = ['nav-item'];
                    if(bc_item.isRoot) {
                        cssClass.push('root');
                    }
                    if(bc_item.active) {
                        cssClass.push('active');
                    }
                    return cssClass.join(' ');
				};

                this.goto = function(item, e) {
                	if (!item.active) {
                        model.itemsModel.loadDataList(item.path);
					}
                };
			};
		};

        var RenderModel = function() {
            var $containerElement,
				render_model = this;

            function getRendererInstance(filename) {
                if (isMarkdownFile(filename)) {
                	return new MarkdownRenderer();
				}
                if (isCodeMirrorFile(filename)) {
                	return new CodeMirrorRenderer();
				}
			}

            this.rdo = ko.observable({});
            this.content = ko.observable(null);
            this.renderer = ko.observable(null);

            this.render = function(data) {
                if (render_model.renderer()) {
                    render_model.renderer().processContent(data);
                }
            };

            this.setRenderer = function(resourceObject) {
                render_model.rdo(resourceObject);
                render_model.renderer(getRendererInstance(resourceObject.attributes.name));
            };

            this.setContainer = function(templateElements) {
                $.each(templateElements, function () {
                    if ($(this).hasClass('fm-renderer-container')) {
                        $containerElement = $(this);
                        return false;
                    }
                });

				render_model.renderer().processDomElements($containerElement);
			};

            var CodeMirrorRenderer = function() {
                this.name = 'codeMirror';
                this.interactive = false;

                var instance = new EditorModel();

                this.processContent = function(data) {
                    instance.render(data);
                    render_model.content(data);
				};

                this.processDomElements = function($container) {
                	if (!instance.instance) {
                        var textarea = $container.find('.fm-cm-renderer-content')[0],
                            extension = getExtension(render_model.rdo().id);

                        instance.createInstance(extension, textarea, {
                            readOnly: 'nocursor',
                            styleActiveLine: false,
                            lineNumbers: false
                        });
					}
				};
			};

            var MarkdownRenderer = function() {
                this.name = 'markdown';
                this.interactive = true;

                var instance = window.markdownit({
                    // Basic options:
                    html: true,
                    linkify: true,
                    typographer: true,

                    // Custom highlight function to apply CSS class `highlight`:
                    highlight: function (str, lang) {
                        if (lang && hljs.getLanguage(lang)) {
                            try {
                                return '<pre class="highlight"><code>' +
                                    hljs.highlight(lang, str, true).value +
                                    '</code></pre>';
                            } catch (__) {}
                        }
                        return '<pre class="highlight"><code>' + instance.utils.escapeHtml(str) + '</code></pre>';
                    },

                    // custom link function to enable <img ...> and file d/ls:
                    replaceLink: function (link, env) {

                        // do not change if link as http:// or ftp:// or mailto: etc.
                        if (link.search('://') !== -1 || startsWith(link, 'mailto:')) {
                            return link;
                        }

                        // define path depending on absolute / relative link type
                        var basePath = (startsWith(link, '/')) ? fileRoot : getDirname(render_model.rdo().id);
                        var path = basePath + ltrim(link, '/');

                        if (isMarkdownFile(path)) {
                            // to open file in preview mode upon click
                            return path;
                        } else {
                            var queryParams = extendRequestParams('GET', {
                                mode: 'readfile',
                                path: path
                            });
                            return buildConnectorUrl(queryParams);
                        }
                    }
                }).use(window.markdownitReplaceLink);

                this.processContent = function(data) {
                    var result = instance.render(data);
                    render_model.content(result);
                    setLinksBehavior();
                };

                this.processDomElements = function($container) {};

                function setLinksBehavior() {
                    // add onClick events to local .md file links (to perform AJAX previews)
                    $containerElement.find('a').each(function() {
                        var href = $(this).attr('href'),
                            editor = fmModel.previewModel.editor;

                        if (editor.enabled() && editor.isInteractive()) {
                            // prevent user from losing unsaved changes in preview mode
                            // in case of clicking on a link that jumps off the page
                            $(this).off('click');
                            $(this).on('click', function () {
                                return false; // prevent onClick event
                            });
                        } else {
                            if (href.search('://') !== -1 || startsWith(href, 'mailto:')) {
                                return; // do nothing
                            }

                            if (isMarkdownFile(href)) {
                                // open file in preview mode for clicked link
                                $(this).on('click', function (e) {
                                    getItemInfo(href).then(function(response) {
                                        if(response.data) {
                                            getDetailView(response.data);
                                        }
                                    });

                                    return false; // prevent onClick event
                                });
                            }
                        }
                    });
				}
			};
        };

        var EditorModel = function() {
        	var editor_model = this,
				delayedContent = null;

        	this.instance = null;
        	this.enabled = ko.observable(false);
        	this.content = ko.observable(null);
            this.mode = ko.observable(null);
            this.isInteractive = ko.observable(false);

            this.mode.subscribe(function(mode) {
                if (mode) {
                    editor_model.instance.setOption('mode', mode);
                    if (delayedContent) {
                        drawContent(delayedContent);
                        delayedContent = null;
                    }
                }
            });

            this.render = function(content) {
            	if (editor_model.mode()) {
                    drawContent(content);
				} else {
                    delayedContent = content;
				}
            };

            this.createInstance = function(extension, element, options) {
                var cm,
                    defaults = {
                        readOnly: 'nocursor',
                        styleActiveLine: false,
                        viewportMargin: Infinity,
                        lineNumbers: config.editor.lineNumbers,
                        lineWrapping: config.editor.lineWrapping,
                        theme: config.editor.theme,
                        matchBrackets: config.editor.matchBrackets,
                        extraKeys: {
                            'F11': function (cm) {
                                cm.setOption('fullScreen', !cm.getOption('fullScreen'));
                            },
                            'Esc': function (cm) {
                                if (cm.getOption('fullScreen')) cm.setOption('fullScreen', false);
                            }
                        }
                    };

                cm = CodeMirror.fromTextArea(element, $.extend({}, defaults, options));

                cm.on('changes', function(cm, change) {
                    editor_model.content(cm.getValue());
                });

                editor_model.instance = cm;

                includeAssets(extension);
            };

            function drawContent(content) {
                editor_model.enabled(true);
                editor_model.instance.setValue(content);
				// to make sure DOM is ready to render content
                setTimeout(function() {
                	editor_model.instance.refresh();
                }, 0);
            }

            function includeAssets(extension) {
                var assets = [],
                	currentMode = 'default';

                // highlight code according to extension file
                if (config.editor.codeHighlight) {
                    if (extension === 'js') {
                        assets.push('/libs/CodeMirror/mode/javascript/javascript.js');
                        currentMode = 'javascript';
                    }
                    if (extension === 'css') {
                        assets.push('/libs/CodeMirror/mode/css/css.js');
                        currentMode = 'css';
                    }
                    if (extension === 'html') {
                        assets.push('/libs/CodeMirror/mode/xml/xml.js');
                        currentMode = 'text/html';
                    }
                    if (extension === 'xml') {
                        assets.push('/libs/CodeMirror/mode/xml/xml.js');
                        currentMode = 'application/xml';
                    }
                    if (extension === 'php') {
                        assets.push('/libs/CodeMirror/mode/htmlmixed/htmlmixed.js');
                        assets.push('/libs/CodeMirror/mode/xml/xml.js');
                        assets.push('/libs/CodeMirror/mode/javascript/javascript.js');
                        assets.push('/libs/CodeMirror/mode/css/css.js');
                        assets.push('/libs/CodeMirror/mode/clike/clike.js');
                        assets.push('/libs/CodeMirror/mode/php/php.js');
                        currentMode = 'application/x-httpd-php';
                    }
                    if (extension === 'java') {
                        assets.push('/libs/CodeMirror/mode/clike/clike.js');
                        currentMode = 'text/x-java';
                    }
                    if (extension === 'sql') {
                        assets.push('/libs/CodeMirror/mode/sql/sql.js');
                        currentMode = 'text/x-mysql';
                    }
                    if (extension === 'md') {
                        assets.push('/libs/CodeMirror/addon/mode/overlay.js');
                        assets.push('/libs/CodeMirror/mode/xml/xml.js');
                        assets.push('/libs/CodeMirror/mode/markdown/markdown.js');
                        assets.push('/libs/CodeMirror/mode/gfm/gfm.js');
                        assets.push('/libs/CodeMirror/mode/javascript/javascript.js');
                        assets.push('/libs/CodeMirror/mode/css/css.js');
                        assets.push('/libs/CodeMirror/mode/htmlmixed/htmlmixed.js');
                        assets.push('/libs/CodeMirror/mode/clike/clike.js');
                        assets.push('/libs/CodeMirror/mode/shell/shell.js');
                        assets.push('/libs/CodeMirror/mode/meta.js');
                        currentMode = 'gfm';
                    }
                    if (extension === 'sh') {
                        assets.push('/libs/CodeMirror/addon/mode/overlay.js');
                        assets.push('/libs/CodeMirror/mode/markdown/markdown.js');
                        assets.push('/libs/CodeMirror/mode/gfm/gfm.js');
                        assets.push('/libs/CodeMirror/mode/javascript/javascript.js');
                        assets.push('/libs/CodeMirror/mode/css/css.js');
                        assets.push('/libs/CodeMirror/mode/htmlmixed/htmlmixed.js');
                        assets.push('/libs/CodeMirror/mode/clike/clike.js');
                        assets.push('/libs/CodeMirror/mode/meta.js');
                        assets.push('/libs/CodeMirror/mode/shell/shell.js');
                        currentMode = 'shell';
                    }
                }

                if(assets.length) {
                    assets.push(function() {
                    	// after all required assets are loaded
                        editor_model.mode(currentMode);
					});
                    loadAssets(assets);
                } else {
                    editor_model.mode(currentMode);
				}
			}
		};

        var DragAndDropModel = function() {
            var drag_model = this,
                restrictedCssClass = 'drop-restricted',
                $dragHelperTemplate = $('#drag-helper-template');

            this.items = [];
            this.hoveredItem = null;
            this.dragHelper = null;
            this.isScrolling = false;
            this.isScrolled = false;
            this.hoveredCssClass = 'drop-hover';

            this.makeDraggable = function(item, element) {
                if(item.rdo.type === 'file' || item.rdo.type === 'folder') {
                    $(element).draggable({
                        distance: 3,
                        cursor: 'pointer',
                        cursorAt: {
                            left: Math.floor($dragHelperTemplate.width() / 2),
                            bottom: 15
                        },
                        scroll: false,
                        appendTo: $wrapper,
                        containment: $container,
                        refreshPositions: false,
                        helper: function () {
                            var $cloned,
                                iconClass;

                            if (model.fetchSelectedItems(item.constructor.name).length > 1) {
                                iconClass = 'ico_multiple';
                            } else {
                                iconClass = (item.rdo.type === 'folder')
                                    ? 'ico_folder'
                                    : 'ico_file ico_ext_' + getExtension(item.rdo.id);
                            }

                            $cloned = $dragHelperTemplate.children('.drag-helper').clone();
                            $cloned.find('.clip').addClass(iconClass);

                            drag_model.dragHelper = $cloned;
                            return $cloned;
                        },
                        start: function(event, ui) {
                            drag_model.items = model.fetchSelectedItems(item.constructor.name);
                        },
                        drag: function(event, ui) {
                            $(this).draggable('option', 'refreshPositions', drag_model.isScrolling || drag_model.isScrolled);
                            drag_model.isScrolled = false;
                        },
                        stop: function(event, ui) {
                            drag_model.items = [];
                            drag_model.dragHelper = null;
                        }
                    });
                }
            };

            this.makeDroppable = function(targetItem, element) {
                if(targetItem.rdo.type === 'folder' || targetItem.rdo.type === 'parent') {
                    $(element).droppable({
                        tolerance: 'pointer',
                        enableExtendedEvents: targetItem instanceof ItemObject,
                        accept: function ($draggable) {
                            var dragItem = ko.dataFor($draggable[0]),
                                type = dragItem ? dragItem.rdo.type : null;

                            return (type === 'file' || type === 'folder');
                        },
                        over: function (event, ui) {
                            // prevent "over" event fire before "out" event
                            // http://stackoverflow.com/a/28457286/7095038
                            setTimeout(function () {
                                markHovered(null);
                                markRestricted(ui.helper, false);

                                if (!isDropAllowed(targetItem)) {
                                    markRestricted(ui.helper, true);
                                }
                                markHovered(targetItem);
                            }, 0);
                        },
                        out: function (event, ui) {
                            markHovered(null);
                            markRestricted(ui.helper, false);
                        },
                        drop: function (event, ui) {
                            markHovered(null);

                            if (!isDropAllowed(targetItem)) {
                                return false;
                            }

                            processMultipleActions(drag_model.items, function (i, itemObject) {
                                return moveItem(itemObject.rdo, targetItem.id);
                            });
                        }
                    });
                }
            };

            // check whether draggable items can be accepted by target item
            function isDropAllowed(targetItem) {
                var matches = $.grep(drag_model.items, function(itemObject, i) {
                    if (targetItem.rdo.type === 'folder' || targetItem.rdo.type === 'parent') {
                        // drop folder inside descending folders (filetree)
                        if (startsWith(targetItem.rdo.id, itemObject.rdo.id)) {
                            return true;
                        }
                        // drop items inside the same folder (filetree)
                        if (targetItem.rdo.id === getClosestNode(itemObject.rdo.id)) {
                            return true;
                        }
                    }
                    // drop item to itself
                    return (itemObject.id === targetItem.id);
                });
                // prevent on moving (to) protect folder or to the one of selected items
                return (targetItem.rdo.attributes.writable && matches.length === 0);
            }

            // mark item as hovered if it accepts draggable item
            function markHovered(item) {
                if (drag_model.hoveredItem !== null) {
                    drag_model.hoveredItem.dragHovered(false);
                }
                drag_model.hoveredItem = item;
                if (item) {
                    item.dragHovered(true);
                }
            }

            // mark helper as restricted if target item doesn't accept draggable item
            function markRestricted($helper, flag) {
                if (flag) {
                    $helper.addClass(restrictedCssClass);
                } else {
                    $helper.removeClass(restrictedCssClass);
                }
            }
        };

        var SelectionModel = function() {
            this.unselect = false;
		};

		this.treeModel = new TreeModel();
		this.itemsModel = new ItemsModel();
		this.tableViewModel = new TableViewModel();
        this.previewModel = new PreviewModel();
		this.headerModel = new HeaderModel();
		this.summaryModel = new SummaryModel();
		this.filterModel = new FilterModel();
		this.searchModel = new SearchModel();
		this.clipboardModel = new ClipboardModel();
		this.breadcrumbsModel = new BreadcrumbsModel();
        this.ddModel = new DragAndDropModel();
        this.selectionModel = new SelectionModel();
	};


	/*---------------------------------------------------------
	 Helper functions
	 ---------------------------------------------------------*/

	// Wrapper for translate method
	var lg = function(key) {
		return langModel.translate(key);
	};

	var sortItems = function(items) {
		var sortOrder = (fmModel.viewMode() === 'list') ? fmModel.itemsModel.listSortOrder() : configSortOrder;
		var sortParams = {
			natural: true,
			order: sortOrder === 'asc' ? 1 : -1,
			cases: false
		};

		items.sort(function(a, b) {
			var sortReturnNumber,
				aa = getSortSubject(a),
				bb = getSortSubject(b);

			if (aa === bb) {
				sortReturnNumber = 0;
			} else {
				if (aa === undefined || bb === undefined) {
					sortReturnNumber = 0;
				} else {
					if(!sortParams.natural || (!isNaN(aa) && !isNaN(bb))) {
						sortReturnNumber = aa < bb ? -1 : (aa > bb ? 1 : 0);
					} else {
						sortReturnNumber = naturalCompare(aa, bb);
					}
				}
			}
			// lastly assign asc/desc
			sortReturnNumber *= sortParams.order;
			return sortReturnNumber;
		});

		/**
		 * Get the string/number to be sorted by checking the array value with the criterium.
		 * @item KO or treeNode object
		 */
		function getSortSubject(item) {
			var sortBy,
				sortField = configSortField;

			if(fmModel.viewMode() === 'list') {
				sortField = fmModel.itemsModel.listSortField();
			}

			switch(sortField) {
				case 'type':
					sortBy = item.cdo.extension || '';
					break;
				case 'size':
					sortBy = item.rdo.attributes.size;
					break;
				case 'modified':
					sortBy = item.rdo.attributes.modified;
					break;
				case 'dimensions':
					sortBy = item.cdo.dimensions || '';
					break;
				default:
					sortBy = item.rdo.attributes.name;
			}

			// strings should be ordered in lowercase (unless specified)
			if (typeof sortBy === 'string') {
				if (!sortParams.cases) {
					sortBy = sortBy.toLowerCase();
				}
				// spaces/newlines
				sortBy = sortBy.replace(/\s+/g, ' ');
			}
			return sortBy;
		}

		/**
		 * Compare strings using natural sort order
		 * http://web.archive.org/web/20130826203933/http://my.opera.com/GreyWyvern/blog/show.dml/1671288
		 */
		function naturalCompare(a, b) {
			var aa = chunkify(a.toString()),
				bb = chunkify(b.toString());
			for (var x = 0; aa[x] && bb[x]; x++) {
				if (aa[x] !== bb[x]) {
					var c = Number(aa[x]),
						d = Number(bb[x]);
					if (c == aa[x] && d == bb[x]) {
						return c - d;
					} else {
						return aa[x] > bb[x] ? 1 : -1;
					}
				}
			}
			return aa.length - bb.length;
		}

		/**
		 * Split a string into an array by type: numeral or string
		 */
		function chunkify(t) {
			var tz = [], x = 0, y = -1, n = 0, i, j;
			while (i = (j = t.charAt(x++)).charCodeAt(0)) {
				var m = (i == 46 || (i >=48 && i <= 57));
				if (m !== n) {
					tz[++y] = '';
					n = m;
				}
				tz[y] += j;
			}
			return tz;
		}

		// handle folders position
		var folderItems = [];
		var i = items.length;
		while(i--) {
			if(items[i].rdo.type === 'folder') {
				folderItems.push(items[i]);
				items.splice(i, 1);
			}
		}
		if(config.options.folderPosition !== 'top') {
			folderItems.reverse();
		}
		for(var k = 0, fl = folderItems.length; k < fl; k++) {
			if(config.options.folderPosition === 'top') {
				items.unshift(folderItems[k]);
			} else {
				items.push(folderItems[k]);
			}
		}

		return items;
	};

	// Test if a given url exists
	var file_exists = function(url) {
		return $.ajax({
			type: 'HEAD',
			url: url
		});
	};

	// Retrieves config settings from config files
	var loadConfigFile = function (type) {
		var url = null;
		type = (typeof type === 'undefined') ? 'user' : type;

		if(type === 'user') {
			if(_url_.param('config')) {
				url = fm.settings.baseUrl + '/config/' + _url_.param('config');
			} else {
				url = fm.settings.configUrl ? fm.settings.configUrl : fm.settings.baseUrl + '/config/filemanager.config.json'; // if configUrl is defined
			}
		} else {
			url = fm.settings.baseUrl + '/config/filemanager.config.default.json';
		}

		return $.ajax({
			type: 'GET',
			url: url,
			dataType: 'json',
			cache: false,
			error: function(response) {
				fm.error('Given config file (' + url + ') does not exist!');
			}
		});
	};

	// Loads a given js/css files dynamically into header
	var loadAssets = function(assets) {
        for (var i = 0, l = assets.length; i < l; i++) {
			if(typeof assets[i] === 'string') {
                assets[i] = fm.settings.baseUrl + assets[i];
			}
        }

        toast.apply(this, assets);
	};

	// Loads a given js template file into header if not already included
	var loadTemplate = function(id, data) {
		return $.ajax({
			type: 'GET',
			url: fm.settings.baseUrl + '/src/templates/' + id + '.html',
			error: handleAjaxError
		});
	};

	// Converts bytes to KB, MB, or GB as needed for display
	var formatBytes = function(bytes, round) {
		if(!bytes) return '';
		round = round || false;
		var n = parseFloat(bytes);
		var d = parseFloat(round ? 1000 : 1024);
		var c = 0;
		var u = [lg('unit_bytes'), lg('unit_kb'), lg('unit_mb'), lg('unit_gb')];

		while(true) {
			if(n < d) {
				n = Math.round(n * 100) / 100;
				return n + ' ' + u[c];
			} else {
				n /= d;
				c += 1;
			}
		}
	};

    // Converts UNIX timestamp to formatted datetime string
    var formatTimestamp = function (datetime) {
        var isString = typeof datetime === "string";
        var isInteger = typeof datetime === "number" && Math.floor(datetime) === datetime;

        // invalid argument
        if (!(isString || isInteger)) return '';

        // value looks like seconds, while Date() accepts milliseconds
        if (isInteger && datetime < 10000000000) {
            datetime = datetime * 1000
        }

        var date = new Date(datetime);

        // invalid Date() object, display datetime without formatting
        if (!(date instanceof Date) || isNaN(date)) {
            return datetime;
        }

        // Timezone support requires "iana-tz-data" package:
        // https://github.com/globalizejs/globalize/blob/master/README.md#3-iana-time-zone-data
        return globalize.formatDate(date, config.formatter.datetime);
    };

    // Format server-side response single error object
    var formatServerError = function(errorObject) {
        var message;
        // look for message in case an error CODE is provided
        if (langModel.getLang() && lg(errorObject.title)) {
            message = lg(errorObject.title);
            $.each(errorObject.meta.arguments, function(i, argument) {
                message = message.replace('%s', argument);
            });
        } else {
            message = errorObject.title;
        }
        return message;
    };

    // Handle JSON server errors.
    var handleJsonErrors = function(errors) {
		fm.console('ERROR JSON', errors);

		$.each(errors, function (i, errorObject) {
			fm.error(formatServerError(errorObject));

			if (errorObject.meta.redirect) {
				window.location.href = errorObject.meta.redirect;
			}
		});
    };

	// Handle ajax request errors.
	var handleAjaxError = function(xhr) {
		var errorMessage;

		if ($.isPlainObject(xhr) && xhr.responseText) {
			var isJSON = (xhr.getResponseHeader('content-type') === 'application/json');

            // on "readfile" API request (dataType === 'text')
            if (!xhr.responseJSON && isJSON) {
                xhr.responseJSON = $.parseJSON(xhr.responseText);
            }

            if ($.isPlainObject(xhr.responseJSON) && xhr.responseJSON.errors) {
                handleJsonErrors(xhr.responseJSON.errors);
			} else {
                errorMessage = lg('ERROR_SERVER') + ' ' + xhr.responseText;
			}
		} else {
			// $.Deferred().reject() case e.g.
            errorMessage = xhr;
		}

		if (errorMessage) {
            fm.console('ERROR TEXT', errorMessage);
            fm.error(errorMessage);
		}
	};

    // Check if capability is allowed
    function hasCapability(capability) {
        return capabilities.indexOf(capability) > -1;
    }

    // Test if resource object has capability
    function isObjectCapable(resourceObject, capability) {
        if (!hasCapability(capability)) return false;
        if (capability === 'select' && resourceObject.type === 'folder') return false;
        if (capability === 'extract') {
            var extension = getExtension(resourceObject.attributes.name);
            return (resourceObject.type === 'file' && extension === 'zip');
        }
        if (capability === 'download' && resourceObject.type === 'folder') {
            return (config.options.allowFolderDownload === true);
        }
        if (typeof(resourceObject.attributes.capabilities) !== 'undefined') {
            return $.inArray(capability, resourceObject.attributes.capabilities) > -1
        }
        return true;
    }

    // http://stackoverflow.com/questions/3390930/any-way-to-make-jquery-inarray-case-insensitive
    (function($){
        $.extend({
            // Case insensative $.inArray (http://api.jquery.com/jquery.inarray/)
            // $.inArrayInsensitive(value, array [, fromIndex])
            //  value (type: String)
            //    The value to search for
            //  array (type: Array)
            //    An array through which to search.
            //  fromIndex (type: Number)
            //    The index of the array at which to begin the search.
            //    The default is 0, which will search the whole array.
            inArrayInsensitive: function(elem, arr, i){
                // not looking for a string anyways, use default method
                if (typeof elem !== 'string'){
                    return $.inArray.apply(this, arguments);
                }
                // confirm array is populated
                if (arr){
                    var len = arr.length;
                        i = i ? (i < 0 ? Math.max(0, len + i) : i) : 0;
                    elem = elem.toLowerCase();
                    for (; i < len; i++){
                        if (i in arr && arr[i].toLowerCase() == elem){
                            return i;
                        }
                    }
                }
                // stick with inArray/indexOf and return -1 on no match
                return -1;
            }
        });
    })(jQuery);

	// Test if file is authorized, based on extension only
	var isAuthorizedFile = function(filename) {
		var ext = getExtension(filename);

		if (config.security.extensions.ignoreCase) {
		    if(config.security.extensions.policy === 'ALLOW_LIST') {
			    if($.inArrayInsensitive(ext, config.security.extensions.restrictions) !== -1) return true;
		    }
		    if(config.security.extensions.policy === 'DISALLOW_LIST') {
			    if($.inArrayInsensitive(ext, config.security.extensions.restrictions) === -1) return true;
		    }
		} else {
		    if(config.security.extensions.policy === 'ALLOW_LIST') {
			    if($.inArray(ext, config.security.extensions.restrictions) !== -1) return true;
		    }
		    if(config.security.extensions.policy === 'DISALLOW_LIST') {
			    if($.inArray(ext, config.security.extensions.restrictions) === -1) return true;
		    }
		}

		return false;
	};

	// Test if path is dir
	var isFile = function(path) {
		return path.charAt(path.length - 1) !== '/';
	};

	// Replace all leading or trailing chars with an empty string
	var trim = function(string, char) {
		var regExp = new RegExp('^' + char + '+|' + char + '+$', 'g');
		return string.replace(regExp, '');
	};

	// Replace all leading chars with an empty string
	var ltrim = function(string, char) {
		var regExp = new RegExp('^' + char + '+', 'g');
		return string.replace(regExp, '');
	};

	// Replace all trailing chars with an empty string
	var rtrim = function(string, char) {
		var regExp = new RegExp(char + '+$', 'g');
		return string.replace(regExp, '');
	};

	var startsWith = function(string, searchString, position) {
		position = position || 0;
		return string.substr(position, searchString.length) === searchString;
	};

	var encodePath = function(path) {
		var parts = [];
		$.each(path.split('/'), function(i, part) {
			parts.push(encodeURIComponent(part));
		});
		return parts.join('/');
	};

	// invert backslashes and remove duplicated ones
    var normalizePath = function(path) {
        return path.replace(/\\/g, '/').replace(/\/+/g, '/');
    };

	// return filename extension
	var getExtension = function(filename) {
		if(filename.split('.').length === 1) {
			return '';
		}
		return filename.split('.').pop().toLowerCase();
	};

	// return filename without extension
	var getFilename = function(filename) {
		if(filename.lastIndexOf('.') !== -1) {
			return filename.substring(0, filename.lastIndexOf('.'));
		} else {
			return filename;
		}
	};

	// return path without filename
	// "/dir/to/" 		  --> "/dir/to/"
	// "/dir/to/file.txt" --> "/dir/to/"
	var getDirname = function(path) {
		if(path.lastIndexOf('/') !== path.length - 1) {
			return path.substr(0, path.lastIndexOf('/') + 1);
		} else {
			return path;
		}
	};

	// return parent folder for path, if folder is passed it should ends with '/'
	// "/dir/to/"          -->  "/dir/"
	// "/dir/to/file.txt"  -->  "/dir/"
	var getParentDirname = function(path) {
		return path.split('/').reverse().slice(2).reverse().join('/') + '/';
	};

	// return closest node for path
	// "/dir/to/"          -->  "/dir/"
	// "/dir/to/file.txt"  -->  "/dir/to/"
	var getClosestNode = function(path) {
		return path.substring(0, path.slice(0, -1).lastIndexOf('/')) + '/';
	};

	// Test if is editable file
	var isEditableFile = function(filename) {
		return ($.inArray(getExtension(filename), config.editor.extensions) !== -1);
	};

	// Test if is image file
	var isImageFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.image.extensions) !== -1);
	};

	// Test if file is supported web video file
	var isVideoFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.video.extensions) !== -1);
	};

	// Test if file is supported web audio file
	var isAudioFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.audio.extensions) !== -1);
	};

    // Test if file is supported by Only Office viewer
    var isOnlyOfficeFile = function(filename) {
        return ($.inArray(getExtension(filename), config.viewer.onlyoffice.extensions) !== -1);
    };

	// Test if file is openable in iframe
	var isIFrameFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.iframe.extensions) !== -1);
	};

	// Test if file is opendoc file
	// Supported file types: http://viewerjs.org/examples/
	var isOpenDocFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.opendoc.extensions) !== -1);
	};

	// Test if file is supported by Google Docs viewer
	// Supported file types: http://stackoverflow.com/q/24325363/1789808
	var isGoogleDocsFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.google.extensions) !== -1);
	};

	// Test if file is supported by CodeMirror renderer
	var isCodeMirrorFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.codeMirrorRenderer.extensions) !== -1);
	};

	// Test if file is supported by Markdown-it renderer, which renders .md files to HTML
	var isMarkdownFile = function(filename) {
		return ($.inArray(getExtension(filename), config.viewer.markdownRenderer.extensions) !== -1);
	};

    var extendRequestParams = function(method, parameters) {
        var methodParams,
            configParams = config.api.requestParams;

        method = method.toUpperCase();

        if ($.isPlainObject(configParams)) {
            methodParams = configParams[method];

            if ($.isPlainObject(methodParams) && !$.isEmptyObject(methodParams)) {
                var extendParams = $.extend({}, configParams['MIXED'] || {}, methodParams);

                // append params to serialized form
                if (method === 'POST' && $.isArray(parameters)) {
                    $.each(extendParams, function(key, value) {
                        parameters.push({
                            name: key,
                            value: value
                        });
                    });
                } else {
                    parameters = $.extend({}, parameters, extendParams);
                }
            }
        }

        parameters = fm.settings.callbacks.beforeSetRequestParams(method, parameters);
        return parameters;
	};

	var buildAjaxRequest = function(method, parameters, dataType) {
        dataType = (typeof dataType === 'undefined') ? 'json' : dataType;

        if (fm.settings.callbacks.beforeSendRequest(method, parameters) === false) {
            return $.Deferred().reject(lg('NOT_ALLOWED'));
		}

        return $.ajax({
            type: method,
            cache: false,
            url: buildConnectorUrl(),
            dataType: dataType,
            data: extendRequestParams(method, parameters)
        });
	};

  var getFilteredFileExtensions = function() {
    if (_url_.param('filter')) {
      if (config.filter[_url_.param('filter')] !== undefined) {
        var shownExtensions = config.filter[_url_.param('filter')];
      }
    }
    return shownExtensions;
  };

	var buildConnectorUrl = function(params) {
		var defaults = {
			time: new Date().getTime()
		};
		var queryParams = $.extend({}, params || {}, defaults);
		return apiConnector + '?' + $.param(queryParams);
	};

	// Build url to preview files
	var createPreviewUrl = function(resourceObject, encode) {
		var previewUrl,
			objectPath = resourceObject.attributes.path;

        if (config.viewer.absolutePath && objectPath) {
            if (encode) {
                objectPath = encodePath(objectPath);
            }
            previewUrl = buildAbsolutePath(objectPath, false);
        } else {
            var queryParams = extendRequestParams('GET', {
                mode: 'readfile',
                path: resourceObject.id
            });
            previewUrl = buildConnectorUrl(queryParams);
        }

        previewUrl = fm.settings.callbacks.beforeCreatePreviewUrl(resourceObject, previewUrl);
		return previewUrl;
	};

	// Build url to display image or its thumbnail
    var createImageUrl = function (resourceObject, thumbnail, disableCache) {
        var imageUrl;
        if (isImageFile(resourceObject.id) &&
            resourceObject.attributes.readable && (
			(thumbnail && config.viewer.image.showThumbs) ||
			(!thumbnail && config.viewer.image.enabled === true)
		)) {
            if (config.viewer.absolutePath && !thumbnail && resourceObject.attributes.path) {
                imageUrl = buildAbsolutePath(encodePath(resourceObject.attributes.path), disableCache);
            } else {
                var queryParams = {path: resourceObject.id};
                if (getExtension(resourceObject.id) === 'svg') {
                    queryParams.mode = 'readfile';
                } else {
                    queryParams.mode = 'getimage';
                    if (thumbnail) {
                        queryParams.thumbnail = 'true';
                    }
                }
                queryParams = extendRequestParams('GET', queryParams);
                imageUrl = buildConnectorUrl(queryParams);
            }
            imageUrl = fm.settings.callbacks.beforeCreateImageUrl(resourceObject, imageUrl);
        }
        return imageUrl;
    };

	var buildAbsolutePath = function(path, disableCache) {
		var url = (typeof config.viewer.previewUrl === 'string') ? config.viewer.previewUrl : location.origin;
        url = trim(url, '/') + path;
		// add timestamp-based query parameter to disable browser caching
		if (disableCache) {
            url += '?time=' + (new Date().getTime());
		}
		return url;
	};

	var createCopyUrl = function(resourceObject) {
		function encodeCopyUrl(path) {
            return (config.clipboard.encodeCopyUrl) ? encodePath(path) : path;
		}

        if(config.viewer.absolutePath && resourceObject.attributes.path) {
            var path = encodeCopyUrl(resourceObject.attributes.path);
            return buildAbsolutePath(path, false);
        } else {
            var path = encodeCopyUrl(resourceObject.id),
				mode = (resourceObject.type === 'folder') ? 'readfolder' : 'readfile';
            return apiConnector + '?path=' + path + '&mode=' + mode;
        }
	};

	// Returns container for filetree or fileinfo section based on scrollbar plugin state
	var getSectionContainer = function($section) {
		// if scrollbar plugin is enabled
		if (config.customScrollbar.enabled) {
			return $section.find('.mCSB_container');
		} else {
			return $section;
		}
	};

	// Handle multiple actions in loop with deferred object
	var processMultipleActions = function(items, callbackFunction, finishCallback) {
        var ProcessingLog = function() {
            this.total = 0;
            this.succeed = 0;
            this.failure = 0;
            this.processed = 0;

            this.getProgress = function() {
                return Math.round((this.processed / this.total) * 100);
            };

            this.getProgressSucceed = function() {
                return Math.round((this.succeed / this.total) * 100);
            };

            this.getProgressFailure = function() {
                return Math.round((this.failure / this.total) * 100);
            };

            this.getMessage = function() {
                return lg('successful_processed').replace('%s', this.succeed).replace('%s', this.total);
            };

            this.succeeded = function() {
                this.succeed++;
                this.processed++;
            };

            this.failed = function() {
                this.failure++;
                this.processed++;
            };

            this.isProcessed = function() {
                return this.processed === this.total;
            };
        };

        var log,
			process = new ProcessingLog(),
            deferred = $.Deferred().resolve();

        process.total = items.length;
		if (process.total > 1) {
            log = fm.write(process.getMessage(), {
                delay: 0,
                logMessageTemplate: function(message) {
                    var progress = process.getProgress(),
                        animateCss = process.isProcessed() ? 'striped' : 'striped animated';

                    return '<div>' + message + '</div>' +
                        '<div class="progress">' +
							'<div class="progress-counter">' + process.getProgress() + '%</div>' +
							'<div class="progress-bar ' + animateCss + '">' +
								'<div class="progress-segment progress-succeed" style="width: ' + process.getProgressSucceed() + '%"></div>' +
								'<div class="progress-segment progress-failure" style="width: ' + process.getProgressFailure() + '%"></div>' +
							'</div>' +
                        '</div>';
                }
            });
            log.stick(true);
		}

		$.each(items, function(i, item) {
			deferred = deferred.then(function() {
				return callbackFunction(i, item);
			}).then(function(result) {
				if(result && result.data) {
					process.succeeded();
				} else {
                    process.failed();
				}
                if (log) {
                    log.setMessage(process.getMessage());
                }
			});
		});

		deferred.then(function() {
			if (log && process.isProcessed()) {
                log.stick(false);
				setTimeout(function() {
					log.remove();
				}, 6000);
			}
		});

        deferred.then(function() {
            if (typeof finishCallback === 'function') {
                finishCallback();
			}
        });
	};

	// Clears browser window selection
	var clearSelection = function() {
		if(document.selection && document.selection.empty) {
			document.selection.empty();
		} else if(window.getSelection) {
			var sel = window.getSelection();
			sel.removeAllRanges();
		}
	};

	// Build FileTree and bind events
	function prepareFileTree() {
		if(!config.filetree.enabled) {
			return;
		}

		$filetree.show();

        // Provides support for adjustible columns.
        $splitter.splitter({
            sizeLeft: config.filetree.width,
            minLeft: config.filetree.minWidth,
            minRight: 200
        });
	}

	// Check if plugin instance created inside some context
	function hasContext() {
		return window.opener // window.open()
			|| (window.parent && window.self !== window.parent) // <iframe>
			|| window.tinyMCEPopup // tinyMCE >= 3.0
			|| _url_.param('field_name') // tinymce 4 or colorbox
			|| _url_.param('CKEditor') // CKEditor 3.0 + integration method
			|| _url_.param('ImperaviElementId'); // Imperavi Redactor I >= 10.x.x
	}


	/*---------------------------------------------------------
	 Item Actions
	 ---------------------------------------------------------*/

	// Triggered by clicking the "Select" button in detail views
	// or choosing the "Select" contextual menu option in list views.
	// NOTE: closes the window when finished.
	var selectItem = function(resourceObject) {
		var contextWindow = null,
			previewUrl = createPreviewUrl(resourceObject, true);

        previewUrl = fm.settings.callbacks.beforeSelectItem(resourceObject, previewUrl);

        // tinyMCE > 3.0 integration method
		if(window.tinyMCEPopup) {
			var win = tinyMCEPopup.getWindowArg("window");
			win.document.getElementById(tinyMCEPopup.getWindowArg("input")).value = previewUrl;
			if (typeof(win.ImageDialog) != "undefined") {
				// Update image dimensions
				if (win.ImageDialog.getImageData)
					win.ImageDialog.getImageData();

				// Preview if necessary
				if (win.ImageDialog.showPreviewImage)
					win.ImageDialog.showPreviewImage(previewUrl);
			}
			tinyMCEPopup.close();
			return;
		}

		// tinymce 4 and colorbox
		if(_url_.param('field_name')) {
			parent.document.getElementById(_url_.param('field_name')).value = previewUrl;

			if(typeof parent.tinyMCE !== "undefined") {
				parent.tinyMCE.activeEditor.windowManager.close();
			}
			if(typeof parent.$.fn.colorbox !== "undefined") {
				parent.$.fn.colorbox.close();
			}
		}

		if(_url_.param('ImperaviElementId')) {
			// use Imperavi Redactor I, tested on v.10.x.x
			if (window.opener) {
				// Popup
			} else {
				// Modal (in iframe)
				var elementId = _url_.param('ImperaviElementId'),
					instance = parent.$('#'+elementId).redactor('core.getObject');

				if(instance) {
					instance.modal.close();
					instance.buffer.set(); // for undo action

					if(isImageFile(resourceObject.attributes.name)) {
						instance.insert.html('<img src="' + previewUrl + '">');
					} else {
						instance.insert.html('<a href="' + previewUrl + '">' + resourceObject.attributes.name + '</a>');
					}
				}
			}
		}

        // CKEditor 3.0 + integration method
		if(_url_.param('CKEditor')) {
			if (window.opener) {
				// Popup
				window.opener.CKEDITOR.tools.callFunction(_url_.param('CKEditorFuncNum'), previewUrl);
			} else {
				// Modal (in iframe)
				parent.CKEDITOR.tools.callFunction(_url_.param('CKEditorFuncNum'), previewUrl);
				parent.CKEDITOR.tools.callFunction(_url_.param('CKEditorCleanUpFuncNum'));
			}
		}

        // FCKEditor 2.0 integration method
		// todo: https://docs.cksource.com/FCKeditor_2.x/Developers_Guide/JavaScript_API
		if(window.opener && typeof window.opener.SetUrl === 'function') {
			if(resourceObject.attributes.width) {
				var p = previewUrl;
				var w = resourceObject.attributes.width;
				var h = resourceObject.attributes.height;
				window.opener.SetUrl(p,w,h);
			} else {
				window.opener.SetUrl(previewUrl);
			}
		}

		// define context window if any
        if (window.opener) {
            contextWindow = window.opener;
        }
        if (window.parent && window.self !== window.parent) {
            contextWindow = window.parent;
        }

		// sending post message to the context window
        if (contextWindow) {
            contextWindow.postMessage(
            	{
                    source: 'richfilemanager',
                    resourceObject: resourceObject,
                    preview_url: previewUrl
                }, '*'
            );
		}

		fm.settings.callbacks.afterSelectItem(resourceObject, previewUrl, contextWindow);
	};

	// Renames the current item and returns the new name.
	// Called by clicking the "Rename" button in detail views
	// or choosing the "Rename" contextual menu option in list views.
	var renameItem = function(resourceObject) {
		var doRename = function(e, ui) {
			var oldPath = resourceObject.id;
			var givenName = ui.getInputValue();
			if(!givenName) {
				// TODO: file/folder message depending on file type
				fm.error(lg('new_filename'));
				return;
			}

			if (! config.options.allowChangeExtensions) {
				var suffix = getExtension(resourceObject.attributes.name);
				if(suffix.length > 0) {
					givenName = givenName + '.' + suffix;
				}
			}

			// File only - Check if file extension is allowed
			if (isFile(oldPath) && !isAuthorizedFile(givenName)) {
				var str = '<p>' + lg('INVALID_FILE_TYPE') + '</p>';
				if(config.security.extensions.policy === 'ALLOW_LIST') {
					str += '<p>' + lg('ALLOWED_FILE_TYPE').replace('%s', config.security.extensions.restrictions.join(', ')) + '.</p>';
				}
				if(config.security.extensions.policy === 'DISALLOW_LIST') {
					str += '<p>' + lg('DISALLOWED_FILE_TYPE').replace('%s', config.security.extensions.restrictions.join(', ')) + '.</p>';
				}
				fm.error(str);
				return;
			}

            buildAjaxRequest('GET', {
                mode: 'rename',
                old: oldPath,
                new: givenName
            }).done(function(response) {
                if(response.data) {
                    var dataObject = response.data;

                    /** handle tree nodes **/
                    var sourceNode = fmModel.treeModel.findByParam('id', oldPath);
                    if(sourceNode) {
                        if(sourceNode.rdo.type === 'folder') {
                            sourceNode.nodeTitle(dataObject.attributes.name);
                            // update object data for the current and all child nodes
                            fmModel.treeModel.actualizeNodeObject(sourceNode, oldPath, dataObject.id);
                        }
                        if(sourceNode.rdo.type === 'file') {
                            var parentNode = sourceNode.parentNode();
                            var newNode = fmModel.treeModel.createNode(dataObject);
                            sourceNode.remove();

                            if(parentNode) {
                                fmModel.treeModel.appendNodes(parentNode, newNode);
                            }
                        }
                    }

                    /** handle view objects **/
                    var parentItem = fmModel.itemsModel.parentItem();
					if (parentItem && parentItem.id === oldPath) {
						// adjust parent item to keep correct reference
                        fmModel.itemsModel.parentItem().id = dataObject.id;
					} else {
                        var sourceItem = fmModel.itemsModel.findByParam('id', oldPath);
                        if(sourceItem) {
                            sourceItem.remove();
                            var newItem = fmModel.itemsModel.createItem(dataObject);
                            fmModel.itemsModel.appendItems(newItem);
						}
					}
                    // ON rename currently open folder
                    if(fmModel.currentPath() === oldPath) {
                        fmModel.itemsModel.loadDataList(dataObject.id);
                    }
                    // ON rename currently previewed file
                    if(fmModel.previewFile() && fmModel.previewModel.rdo().id === oldPath) {
                        fmModel.previewModel.applyObject(dataObject);
                    }

                    ui.closeDialog();
                    if(config.options.showConfirmation) {
                        fm.success(lg('successful_rename'));
                    }
                }
            }).fail(handleAjaxError);
		};

		fm.prompt({
			message: lg('new_filename'),
			value: config.options.allowChangeExtensions ? resourceObject.attributes.name : getFilename(resourceObject.attributes.name),
			okBtn: {
				label: lg('action_rename'),
				autoClose: false,
				click: doRename
			},
			cancelBtn: {
				label: lg('cancel')
			}
		});
	};

	// Move the current item to specified dir and returns the new name.
	// Called by clicking the "Move" button in de tail views
	// or choosing the "Move" contextual menu option in list views.
	var moveItemPrompt = function(objects, successCallback) {
		var doMove = function(e, ui) {
			var targetPath = ui.getInputValue();
			if(!targetPath) {
				fm.error(lg('prompt_foldername'));
				return;
			}
			targetPath = rtrim(targetPath, '/') + '/';
			successCallback(targetPath);
		};

		var objectsTotal = objects.length,
			message = (objectsTotal > 1) ? lg('prompt_move_multiple').replace('%s', objectsTotal) : lg('prompt_move');

		fm.prompt({
			message: message,
			value: fmModel.currentPath(),
			okBtn: {
				label: lg('action_move'),
				autoClose: false,
				click: doMove
			},
			cancelBtn: {
				label: lg('cancel')
			},
			template: {
				dialogInput:
				'<input data-alertify-input type="text" value="" />' +
				'<div class="prompt-info">' + lg('help_move') + '</div>'
			}
		});
	};

	// Copy the current item to specified dir and returns the new name.
	// Called upon paste copied items via clipboard.
    var copyItem = function (resourceObject, targetPath) {
        return buildAjaxRequest('GET', {
            mode: 'copy',
            source: resourceObject.id,
            target: targetPath
        }).done(function (response) {
            if (response.data) {
                var dataObject = response.data;

                fmModel.addElements(dataObject, targetPath);

                alertify.clearDialogs();
                if (config.options.showConfirmation) {
                    fm.success(lg('successful_copied'));
                }
            }
        }).fail(handleAjaxError);
    };

	// Move the current item to specified dir and returns the new name.
	// Called by clicking the "Move" button in detail views
	// or choosing the "Move" contextual menu option in list views.
	var moveItem = function(resourceObject, targetPath) {
	    return buildAjaxRequest('GET', {
            mode: 'move',
            old: resourceObject.id,
            new: targetPath
        }).done(function(response) {
            if(response.data) {
                var dataObject = response.data;

                fmModel.removeElement(resourceObject);
                fmModel.addElements(dataObject, targetPath);

                // ON move currently open folder to another folder
                if(fmModel.currentPath() === resourceObject.id) {
                    fmModel.itemsModel.loadDataList(dataObject.id);
                }
                // ON move currently previewed file
                if(fmModel.previewFile() && fmModel.previewModel.rdo().id === resourceObject.id) {
                    fmModel.previewFile(false);
                }

                alertify.clearDialogs();
                if(config.options.showConfirmation) {
                    fm.success(lg('successful_moved'));
                }
            }
        }).fail(handleAjaxError);
	};

	// Prompts for confirmation, then deletes the current item.
	var deleteItemPrompt = function(objects, successCallback) {
		var objectsTotal = objects.length,
			message = (objectsTotal > 1) ? lg('confirm_delete_multiple').replace('%s', objectsTotal) : lg('confirm_delete');

		fm.confirm({
			message: message,
			okBtn: {
				label: lg('yes'),
				click: function(e, ui) {
					successCallback();
				}
			},
			cancelBtn: {
				label: lg('no')
			}
		});
	};

	// Delete item by path
	var deleteItem = function(path) {
        return buildAjaxRequest('GET', {
            mode: 'delete',
            path: path
        }).done(function(response) {
            if(response.data) {
                var dataObject = response.data;

                fmModel.removeElement(dataObject);

                // ON delete currently open folder
                if(dataObject.type === 'folder' && startsWith(fmModel.currentPath(), dataObject.id)) {
                    var parentFolder = getParentDirname(dataObject.id);
                    fmModel.itemsModel.loadDataList(parentFolder);
                }

                // ON delete currently previewed file
                if(fmModel.previewFile() && fmModel.previewModel.rdo().id === dataObject.id) {
                    fmModel.previewFile(false);
                }

                if(config.options.showConfirmation) {
                    fm.success(lg('successful_delete'));
                }
            }
        }).fail(handleAjaxError);
	};

	// Starts file download process.
	// Called by clicking the "Download" button in detail views
	// or choosing the "Download" contextual menu item in list views.
	var downloadItem = function(resourceObject) {
		var queryParams = {
			mode: 'download',
			path: resourceObject.id
		};
		$.fileDownload(buildConnectorUrl(extendRequestParams('GET', queryParams)), {
			failCallback: function (responseHtml, url, error) {
                var message = $(responseHtml).text();
				var messageJSON = $.parseJSON(message);

                if ($.isPlainObject(messageJSON) && messageJSON.errors) {
                    handleJsonErrors(messageJSON.errors);
                }
			}
		});
	};

	// Save CodeMirror editor content to file
	var saveItem = function(resourceObject) {
		var formParams = $('#fm-js-editor-form').serializeArray();

        buildAjaxRequest('POST', formParams).done(function(response) {
            if(response.data) {
                var dataObject = response.data,
                    preview_model = fmModel.previewModel,
                    content = preview_model.editor.content();

                // update preview object data
                preview_model.rdo(dataObject);

                // assign new content to the viewer and close editor
                preview_model.viewer.content(content);
                preview_model.closeEditor();

                // replace original item with a new one to adjust observable items
                var newItem = fmModel.itemsModel.createItem(dataObject);
                var originalItem = fmModel.itemsModel.findByParam('id', dataObject.id);
                fmModel.itemsModel.objects.replace(originalItem, newItem);

                fm.success(lg('successful_edit'));
            }
        }).fail(handleAjaxError);
	};

    // Perform "readfile" API request
    var previewItem = function(resourceObject) {
        return buildAjaxRequest('GET', {
            mode: 'readfile',
            path: resourceObject.id
        }, 'text').fail(handleAjaxError);
    };

    // Perform "readfolder" API request
    var readFolder = function(targetPath) {
        return buildAjaxRequest('GET', {
            mode: 'readfolder',
            path: targetPath
        }).fail(handleAjaxError);
	};

    // Perform "seekfolder" API request
    var seekFolder = function(targetPath, searchString) {
        return buildAjaxRequest('GET', {
            mode: 'seekfolder',
            path: targetPath,
            string: searchString
        }).fail(handleAjaxError);
	};

    // Perform "getinfo" API request
    var getItemInfo = function(targetPath) {
        return buildAjaxRequest('GET', {
            mode: 'getinfo',
            path: targetPath
        }).fail(handleAjaxError);
	};

	// Display storage summary info
	var summarizeItems = function() {
        return buildAjaxRequest('GET', {
            mode: 'summarize'
        }).done(function(response) {
            if(response.data) {
                var data = response.data.attributes,
                    size = formatBytes(data.size, true);

                if(data.sizeLimit > 0) {
                    var sizeTotal = formatBytes(data.sizeLimit, true);
                    var ratio = data.size * 100 / data.sizeLimit;
                    var percentage = Math.round(ratio * 100) / 100;
                    size += ' (' + percentage + '%) ' + lg('of') + ' ' + sizeTotal;
                }

                fmModel.summaryModel.files(data.files);
                fmModel.summaryModel.folders(data.folders);
                fmModel.summaryModel.size(size);

                fmModel.summaryModel.enabled(true);
                var $summary = $('#summary-popup').clone().show();
                fmModel.summaryModel.enabled(false);

                fm.alert($summary[0].outerHTML);
            }
        }).fail(handleAjaxError);
	};

    // Prompts for confirmation, then extracts the current archive.
    var extractItemPrompt = function(resourceObject) {
        var doExtract = function(e, ui) {
            var targetPath = ui.getInputValue();
            if(!targetPath) {
                fm.error(lg('prompt_foldername'));
                return;
            }
            targetPath = rtrim(targetPath, '/') + '/';

            extractItem(resourceObject, targetPath)
        };

        fm.prompt({
            message: lg('prompt_extract'),
            value: fmModel.currentPath(),
            okBtn: {
                label: lg('action_extract'),
                autoClose: false,
                click: doExtract
            },
            cancelBtn: {
                label: lg('cancel')
            }
        });
    };

    // Extract files and folders from archive.
    // Called by choosing the "Extract" contextual menu option in list views.
    var extractItem = function(resourceObject, targetPath) {
        buildAjaxRequest('POST', {
            mode: 'extract',
            source: resourceObject.id,
            target: targetPath
        }).done(function(response) {
            if(response.data) {
                fmModel.addElements(response.data, targetPath);

                alertify.clearDialogs();
                if(config.options.showConfirmation) {
                    fm.success(lg('successful_extracted'));
                }
            }
        }).fail(handleAjaxError);
    };

	/*---------------------------------------------------------
	 Functions to Retrieve File and Folder Details
	 ---------------------------------------------------------*/

	// Retrieves file or folder info based on the path provided.
	function getDetailView(resourceObject) {
		if(!resourceObject.attributes.readable) {
			fm.error(lg('NOT_ALLOWED_SYSTEM'));
			return false;
		}
		if(resourceObject.type === 'file') {
            fmModel.previewModel.applyObject(resourceObject);
		}
		if(resourceObject.type === 'folder' || resourceObject.type === 'parent') {
            fmModel.previewFile(false);
			fmModel.itemsModel.loadDataList(resourceObject.id);
		}
	}

	// Options for context menu plugin
	function getContextMenuItems(resourceObject) {
        var clipboardDisabled = !fmModel.clipboardModel.enabled(),
            contextMenuItems = {
                select: {name: lg('action_select'), className: 'select'},
                download: {name: lg('action_download'), className: 'download'},
                rename: {name: lg('action_rename'), className: 'rename'},
                move: {name: lg('action_move'), className: 'move'},
                separator1: '-----',
                copy: {name: lg('clipboard_copy'), className: 'copy'},
                cut: {name: lg('clipboard_cut'), className: 'cut'},
                delete: {name: lg('action_delete'), className: 'delete'},
                extract: {name: lg('action_extract'), className: 'extract'},
                copyUrl: {name: lg('copy_to_clipboard'), className: 'copy-url'}
            };

		if(!isObjectCapable(resourceObject, 'download')) delete contextMenuItems.download;
        if(!isObjectCapable(resourceObject, 'select') || !hasContext()) delete contextMenuItems.select;
        if(!isObjectCapable(resourceObject, 'rename') || config.options.browseOnly === true) delete contextMenuItems.rename;
		if(!isObjectCapable(resourceObject, 'delete') || config.options.browseOnly === true) delete contextMenuItems.delete;
		if(!isObjectCapable(resourceObject, 'extract') || config.options.browseOnly === true) delete contextMenuItems.extract;
		if(!isObjectCapable(resourceObject, 'copy') || config.options.browseOnly === true || clipboardDisabled) delete contextMenuItems.copy;
		if(!isObjectCapable(resourceObject, 'move') || config.options.browseOnly === true || clipboardDisabled) {
            delete contextMenuItems.cut;
            delete contextMenuItems.move;
		}

		return contextMenuItems
	}

	// Binds contextual menu to items in list and grid views.
	var performAction = function(action, options, targetObject, selectedObjects) {
		// suppose that target object is part of selected objects while multiple selection
		var objects = selectedObjects ? selectedObjects : [targetObject];

		switch(action) {
			case 'select':
				selectItem(targetObject);
				break;

			case 'download':
				$.each(objects, function(i, itemObject) {
					downloadItem(itemObject);
				});
				break;

			case 'rename':
				renameItem(targetObject);
				break;

			case 'move':
				moveItemPrompt(objects, function(targetPath) {
					processMultipleActions(objects, function(i, itemObject) {
						return moveItem(itemObject, targetPath);
					});
				});
				break;

			case 'delete':
				deleteItemPrompt(objects, function() {
					processMultipleActions(objects, function(i, itemObject) {
						return deleteItem(itemObject.id);
					});
				});
				break;

			case 'extract':
                extractItemPrompt(targetObject);
				break;

			case 'copy':
                fmModel.clipboardModel.copy(objects);
				break;

			case 'cut':
                fmModel.clipboardModel.cut(objects);
				break;

			case 'copyUrl':
                var clipboard = new Clipboard(options.$selected[0], {
                    text: function(trigger) {
                        return createCopyUrl(targetObject);
                    }
                });

                clipboard.on('success', function(e) {
                    fm.success(lg('copied'));
                    clipboard.destroy();
                });
				break;
		}
	};

	// Handling file uploads
	var setupUploader = function() {
		if(config.options.browseOnly) {
			return false;
		}

		// Multiple Uploads
		if(config.upload.multiple) {
			$uploadButton.unbind().click(function() {
				if (!hasCapability('upload')) {
					fm.error(lg('NOT_ALLOWED'));
					return false;
				}

				var allowedFileTypes = null,
					currentPath = fmModel.currentPath(),
					templateContainer = tmpl('tmpl-fileupload-container', {
						folder: lg('current_folder') + currentPath,
						info: lg('upload_files_number_limit').replace('%s', config.upload.maxNumberOfFiles) + ' ' + lg('upload_file_size_limit').replace('%s', formatBytes(config.upload.fileSizeLimit, true)),
						lang: langModel.getTranslations()
					});

				if(config.security.extensions.policy === 'ALLOW_LIST') {
					allowedFileTypes = new RegExp('(\\.|\\/)(' + config.security.extensions.restrictions.join('|') + ')$', 'i');
				}

				fm.dialog({
					message: templateContainer,
					width: 'auto',
					buttons: [{
						type: 'ok',
						label: lg('action_upload'),
						autoClose: false,
						click: function(e, ui) {
							if($dropzone.children('.upload-item').length > 0) {
								$dropzone.find('.button-start').trigger('click');
							} else {
								fm.error(lg('upload_choose_file'));
							}
						}
					},{
						label: lg('action_select'),
						closeOnClick: false,
						click: function(e, ui) {
							$('#fileupload', $uploadContainer).trigger('click');
						}
					},{
						type: 'cancel',
						label: lg('close')
					}]
				});

				var $uploadContainer = $('.fm-fileupload-container'),
					$dropzone = $('.dropzone', $uploadContainer),
					$dropzoneWrapper = $('.dropzone-wrapper', $uploadContainer),
					$totalProgressBar = $('#total-progress', $uploadContainer).children();

				if(config.customScrollbar.enabled) {
					$dropzoneWrapper.mCustomScrollbar({
						theme: config.customScrollbar.theme,
						scrollButtons: {
							enable: config.customScrollbar.button
						},
						advanced: {
							autoExpandHorizontalScroll: true,
							updateOnContentResize: true
						},
						callbacks: {
							onOverflowY: function() {
								$dropzoneWrapper.find('.mCSB_container').css({
									'margin-right': $dropzoneWrapper.find('.mCSB_scrollTools').width()
								});
							},
							onOverflowYNone: function() {
								$dropzoneWrapper.find('.mCSB_container').css({
									'margin-right': 'auto'
								});
							}
						},
						axis: 'y'
					});
				}

				$dropzoneWrapper.on('click', function(e) {
					if(e.target === this || $(e.target).parent()[0] === this || e.target === $dropzone[0] || $(e.target).parent().hasClass('default-message')) {
						$('#fileupload', $uploadContainer).trigger('click');
					}
				});

				/**
				 * Start uploading process.
				 */
				$dropzone.on('click', '.button-start', function(e) {
					var $target = $(this);
					var $buttons = $target.parent().parent();
					var data = $buttons.data();

					data.submit();
					$target.remove();
				});

				/**
				 * Abort uploading process.
				 */
				$dropzone.on('click', '.button-abort', function(e) {
					var $target = $(this),
						$buttons = $target.parent().parent(),
						data = $buttons.data(),
						$node = data.files[0].context;

					data.abort();
					$node.find('.error-message').text(lg('upload_aborted'));
					$node.addClass('aborted');
				});

				/**
				 * Resume uploading if at least one chunk was uploaded.
				 * Otherwise start upload from the very beginning of file.
				 */
				$dropzone.on('click', '.button-resume', function(e) {
					var $target = $(this),
						$buttons = $target.parent().parent(),
						data = $buttons.data(),
						file = data.files[0];

					function resumeUpload(data) {
						$.blueimp.fileupload.prototype.options.add.call($('#fileupload')[0], e, data);
						data.submit();
					}

					if(file.chunkUploaded) {
						var targetPath = currentPath + file.serverName;
                        getItemInfo(targetPath).then(function(response) {
                            if(response.data) {
                                data.uploadedBytes = Number(response.data.attributes.size);
                                if(!data.uploadedBytes) {
                                    file.chunkUploaded = undefined;
                                }
                                resumeUpload(data);
                            }
                        });
					} else {
						resumeUpload(data);
					}
				});

				/**
				 * Remove file from upload query.
				 * Also remove uploaded file chunks if were uploaded.
				 */
				$dropzone.on('click', '.button-remove', function(e) {
					var $target = $(this),
						$buttons = $target.parent().parent(),
						data = $buttons.data(),
						file = data.files[0];

					if(file.chunkUploaded) {
						deleteItem(currentPath + file.serverName);
					}

					$target.closest('.upload-item').remove();
					updateDropzoneView();
				});

				$dropzone.on('click', '.button-info', function(e) {
					var $target = $(this);
					var $node = $target.closest('.upload-item');

					if($node.hasClass('error')) {
						var $message = $node.find('.error-message');
						fm.error($message.text());
					}
				});

                var updateDropzoneView = function () {
                    if ($dropzone.children('.upload-item').length > 0) {
                        $dropzone.addClass('started');
                    } else {
                        $dropzone.removeClass('started');
                    }
                };

                var shownExtensions = fmModel.filterModel.getExtensions();
                if (shownExtensions) {
                    $('#fileupload').attr('accept', shownExtensions.map(function (el) {
                        return '.' + el;
                    }).join());
                }

                $('#fileupload', $uploadContainer)
					.fileupload({
						autoUpload: false,
						sequentialUploads: true,
						dataType: 'json',
						dropZone: $dropzone,
						maxChunkSize: config.upload.chunkSize,
						url: buildConnectorUrl(),
						paramName: config.upload.paramName,
						singleFileUploads: true,
						formData: extendRequestParams('POST', {
                            mode: 'upload',
                            path: currentPath
                        }),
						// validation
						// maxNumberOfFiles works only for single "add" call when "singleFileUploads" is set to "false"
						maxNumberOfFiles: config.upload.maxNumberOfFiles,
						acceptFileTypes: allowedFileTypes,
						maxFileSize: config.upload.fileSizeLimit,
						messages: {
							maxNumberOfFiles: lg('upload_files_number_limit').replace('%s', config.upload.maxNumberOfFiles),
							acceptFileTypes: lg('upload_file_type_invalid'),
							maxFileSize: lg('upload_file_too_big') + ' ' + lg('upload_file_size_limit').replace('%s', formatBytes(config.upload.fileSizeLimit, true))
						},
						// image preview options
						previewMaxHeight: 120,
						previewMaxWidth: 120,
						previewCrop: true
					})

					.on('fileuploadadd', function(e, data) {
						var $items = $dropzone.children('.upload-item');
						$.each(data.files, function (index, file) {
							// skip selected files if total files number exceed "maxNumberOfFiles"
							if($items.length >= config.upload.maxNumberOfFiles) {
								fm.error(lg('upload_files_number_limit').replace('%s', config.upload.maxNumberOfFiles), {
									logClass: 'fileuploadadd',
									unique: true
								});
								return false;
							}
							// to display in item template
							file.formattedSize = formatBytes(file.size);
							var $template = $(tmpl('tmpl-upload-item', {
								file: file,
								lang: langModel.getTranslations(),
								imagesPath: fm.settings.baseUrl + '/libs/jQuery-File-Upload/img'
							}));
							file.context = $template;
							$template.find('.buttons').data(data);
							$template.appendTo($dropzone);
						});
						updateDropzoneView();
					})

					.on('fileuploadsend', function(e, data) {
                        if (fm.settings.callbacks.beforeSendRequest(data.type, data.formData) === false) {
                            $.each(data.files, function (index, file) {
                                var $node = file.context;
                                $node.find('.error-message').text(lg('NOT_ALLOWED'));
                                $node.removeClass('added process').addClass('error');
                            });
                            return false;
                        }

						$.each(data.files, function (index, file) {
							var $node = file.context;
							$node.removeClass('added aborted error').addClass('process');

							// workaround to handle a case while chunk uploading when you may press abort button after
							// uploading is done, but right before "fileuploaddone" event is fired, and try to resume upload
							if(file.chunkUploaded && (data.total === data.uploadedBytes)) {
								$node.remove();
							}
						});
					})

					.on('fileuploadfail', function(e, data) {
                        var error, xhr = data.jqXHR;

                        // handle server-side errors
                        if ($.isPlainObject(xhr.responseJSON) && xhr.responseJSON.errors) {
                            error = formatServerError(xhr.responseJSON.errors[0]);
                        } else {
                            error = lg('upload_failed')
						}

						$.each(data.files, function (index, file) {
							var $node = file.context;
                            $node.removeClass('added process').addClass('error');
                            $node.find('.error-message').text(error);
                            $node.find('.button-start').remove();
						});
					})

					.on('fileuploaddone', function(e, data) {
						var response = data.result;
						$.each(data.files, function (index, file) {
                            if(response && response.data && response.data[index]) {
                                // remove file preview item on success upload
                                file.context.remove();
							}
						});
					})

					.on('fileuploadalways', function(e, data) {
						var response = data.result;
						$.each(data.files, function (index, file) {
							if(response && response.data && response.data[index]) {
								var resourceObject = response.data[index];
								fmModel.removeElement(resourceObject);
								fmModel.addElements(resourceObject, fmModel.currentPath());
							}
						});

						var $items = $dropzone.children('.upload-item');
						// all files in queue are processed
						if($items.filter('.added').length === 0 && $items.filter('.process').length === 0) {
							// all files were successfully uploaded
							if($items.length === 0) {
								alertify.clearDialogs();

								if (config.options.showConfirmation) {
									fm.success(lg('upload_successful_files'));
								}
							}
							// errors occurred
							if($items.filter('.error').length) {
								fm.error(lg('upload_partially') + "<br>" + lg('upload_failed_details'));
							}
						}
						updateDropzoneView();
					})

					.on('fileuploadchunkdone', function (e, data) {
						var response = data.result;
						$.each(data.files, function (index, file) {
							if(response.data && response.data[index]) {
								var resourceObject = response.data[index];
								fmModel.removeElement(resourceObject);
								fmModel.addElements(resourceObject, fmModel.currentPath());

								// get filename from server, it may differ from original
								file.serverName = resourceObject.attributes.name;
								// mark that file has uploaded chunk(s)
								file.chunkUploaded = 1;
							}
						});
					})

					.on('fileuploadprocessalways', function(e, data) {
						$.each(data.files, function (index, file) {
							var $node = file.context;
							// file wasn't added to queue (due to config.upload.maxNumberOfFiles limit e.g.)
							if(typeof $node === 'undefined') {
								return;
							}
							// show preview for image file
							if (file.preview) {
								$node.find('.image').append(file.preview);
								$node.find('.preview').removeClass('file-preview').addClass('image-preview');
							}
							// handle client-side error
							if (file.error) {
								$node.removeClass('added process').addClass('error');
								$node.find('.error-message').text(file.error);
								$node.find('.button-start').remove();
							}
						});
					})

					.on('fileuploadprogress', function (e, data) {
						$.each(data.files, function (index, file) {
							// fill progress bar for single item
							var $node = file.context,
								progress = parseInt(data.loaded / data.total * 100, 10);
							$node.find('.progress-bar').css('width', progress + '%');
						});
					})

					.on('fileuploadprogressall', function (e, data) {
						// fill total progress bar
						var progress = parseInt(data.loaded / data.total * 100, 10);
						$totalProgressBar.css('width', progress + '%');
					});
			});

		// Simple Upload
		} else {
            $uploadButton.unbind().click(function() {
                if(!hasCapability('upload')) {
                    fm.error(lg('NOT_ALLOWED'));
                    return false;
                }

                $('#newfile').trigger('click');
			});

			$uploader
				.fileupload({
					autoUpload: true,
					dataType: 'json',
					url: buildConnectorUrl(),
					paramName: config.upload.paramName,
					maxChunkSize: config.upload.chunkSize
				})

				.on('fileuploadadd', function(e, data) {
					$uploadButton.data(data);
				})

				.on('fileuploadsubmit', function(e, data) {
                    data.formData = extendRequestParams('POST', {
                        mode: 'upload',
                        path: fmModel.currentPath()
                    });
					$uploadButton.addClass('loading').prop('disabled', true);
					$uploadButton.children('span').text(lg('loading_data'));
				})

                .on('fileuploadsend', function(e, data) {
                    if (fm.settings.callbacks.beforeSendRequest(data.type, data.formData) === false) {
                        fm.error(lg('NOT_ALLOWED'));
                        return false;
                    }
                })

				.on('fileuploadalways', function(e, data) {
					$uploadButton.removeData().removeClass('loading').prop('disabled', false);
					$uploadButton.children('span').text(lg('action_upload'));
					var response = data.result;

					if(response && response.data) {
						var resourceObject = response.data[0];
						fmModel.removeElement(resourceObject);
						fmModel.addElements(resourceObject, fmModel.currentPath());

						if(config.options.showConfirmation) {
							fm.success(lg('upload_successful_file'));
						}
					}
				})

				.on('fileuploadchunkdone', function (e, data) {
					var response = data.result;
					if(response.data && response.data[0]) {
						var resourceObject = response.data[0];
						fmModel.removeElement(resourceObject);
						fmModel.addElements(resourceObject, fmModel.currentPath());
					}
				})

				.on('fileuploadfail', function(e, data) {
                    var error, xhr = data.jqXHR;

                    // handle server-side errors
                    if ($.isPlainObject(xhr.responseJSON) && xhr.responseJSON.errors) {
                        error = formatServerError(xhr.responseJSON.errors[0]);
                    } else {
                        error = lg('upload_failed')
                    }

					fm.error(error);
				});
		}
	};

	// call the "constructor" method
	construct();

	$(window).resize(fm.setDimensions);
};
})(jQuery);

// add the plugin to the jQuery.fn object
$.fn.richFilemanager = function(options) {

	// iterate through the DOM elements we are attaching the plugin to
	return this.each(function() {

		// if plugin has not already been attached to the element
		if (undefined === $(this).data('richFilemanager')) {

			/**
			 * Creates a new instance of the plugin
			 * Pass the DOM element and the user-provided options as arguments
			 */
			var plugin = new $.richFilemanagerPlugin(this, options);

			/**
			 * Store a reference to the plugin object
			 * The plugin are available like:
			 * - element.data('richFilemanager').publicMethod(arg1, arg2, ... argn);  for methods
			 * - element.data('richFilemanager').settings.propertyName;  for properties
			 */
			$(this).data('richFilemanager', plugin);
		}
	});
};

// add location.origin for IE
if (!window.location.origin) {
	window.location.origin = window.location.protocol + '//'
		+ window.location.hostname
		+ (window.location.port ? ':' + window.location.port : '');
}
