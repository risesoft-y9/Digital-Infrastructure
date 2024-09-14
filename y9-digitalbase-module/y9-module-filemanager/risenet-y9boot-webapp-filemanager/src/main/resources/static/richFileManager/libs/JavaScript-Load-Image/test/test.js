/*
 * JavaScript Load Image Test
 * https://github.com/blueimp/JavaScript-Load-Image
 *
 * Copyright 2011, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */

/*global window, describe, it, Blob */

;(function (expect, loadImage) {
  'use strict'

  var canCreateBlob = !!window.dataURLtoBlob
  // 80x60px GIF image (color black, base64 data):
  var b64DataGIF = 'R0lGODdhUAA8AIABAAAAAP///ywAAAAAUAA8AAACS4SPqcvtD6' +
                    'OctNqLs968+w+G4kiW5omm6sq27gvH8kzX9o3n+s73/g8MCofE' +
                    'ovGITCqXzKbzCY1Kp9Sq9YrNarfcrvcLDovH5PKsAAA7'
  var imageUrlGIF = 'data:image/gif;base64,' + b64DataGIF
  var blobGIF = canCreateBlob && window.dataURLtoBlob(imageUrlGIF)
  // 1x2px JPEG (color white, with the Exif orientation flag set to 6):
  var b64DataJPEG = '/9j/4AAQSkZJRgABAQEAYABgAAD/4QAiRXhpZgAASUkqAAgAAA' +
                    'ABABIBAwABAAAABgASAAAAAAD/2wBDAAEBAQEBAQEBAQEBAQEB' +
                    'AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQ' +
                    'EBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEB' +
                    'AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQ' +
                    'EBAQEBAQH/wAARCAABAAIDASIAAhEBAxEB/8QAHwAAAQUBAQEB' +
                    'AQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBA' +
                    'QAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAk' +
                    'M2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1' +
                    'hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKj' +
                    'pKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+' +
                    'Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAA' +
                    'AAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAx' +
                    'EEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl' +
                    '8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2' +
                    'hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmq' +
                    'srO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8v' +
                    'P09fb3+Pn6/9oADAMBAAIRAxEAPwD+/iiiigD/2Q=='
  var imageUrlJPEG = 'data:image/jpeg;base64,' + b64DataJPEG
  var blobJPEG = canCreateBlob && window.dataURLtoBlob(imageUrlJPEG)
  function createBlob (data, type) {
    try {
      return new Blob([data], {type: type})
    } catch (e) {
      var BlobBuilder = window.BlobBuilder || window.WebKitBlobBuilder ||
      window.MozBlobBuilder || window.MSBlobBuilder
      var builder = new BlobBuilder()
      builder.append(data.buffer || data)
      return builder.getBlob(type)
    }
  }

  describe('Loading', function () {
    it('Return the img element or FileReader object to allow aborting the image load', function () {
      var img = loadImage(blobGIF, function () {
        return
      })
      expect(img).to.be.an(Object)
      expect(img.onload).to.be.a('function')
      expect(img.onerror).to.be.a('function')
    })

    it('Load image url', function (done) {
      expect(loadImage(imageUrlGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(60)
        done()
      })).to.be.ok()
    })

    it('Load image blob', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(60)
        done()
      })).to.be.ok()
    })

    it('Return image loading error to callback', function (done) {
      expect(loadImage('404', function (img) {
        expect(img).to.be.a(window.Event)
        expect(img.type).to.be('error')
        done()
      })).to.be.ok()
    })

    it('Keep object URL if noRevoke is true', function (done) {
      expect(loadImage(blobGIF, function (img) {
        loadImage(img.src, function (img2) {
          expect(img.width).to.be(img2.width)
          expect(img.height).to.be(img2.height)
          done()
        })
      }, {noRevoke: true})).to.be.ok()
    })

    it('Discard object URL if noRevoke is undefined or false', function (done) {
      expect(loadImage(blobGIF, function (img) {
        loadImage(img.src, function (img2) {
          if (!window.callPhantom) {
            // revokeObjectUrl doesn't seem to have an effect in PhantomJS
            expect(img2).to.be.a(window.Event)
            expect(img2.type).to.be('error')
          }
          done()
        })
      })).to.be.ok()
    })
  })

  describe('Scaling', function () {
    describe('max/min', function () {
      it('Scale to maxWidth', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(40)
          expect(img.height).to.be(30)
          done()
        }, {maxWidth: 40})).to.be.ok()
      })

      it('Scale to maxHeight', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(20)
          expect(img.height).to.be(15)
          done()
        }, {maxHeight: 15})).to.be.ok()
      })

      it('Scale to minWidth', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(160)
          expect(img.height).to.be(120)
          done()
        }, {minWidth: 160})).to.be.ok()
      })

      it('Scale to minHeight', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(320)
          expect(img.height).to.be(240)
          done()
        }, {minHeight: 240})).to.be.ok()
      })

      it('Scale to minWidth but respect maxWidth', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(160)
          expect(img.height).to.be(120)
          done()
        }, {minWidth: 240, maxWidth: 160})).to.be.ok()
      })

      it('Scale to minHeight but respect maxHeight', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(160)
          expect(img.height).to.be(120)
          done()
        }, {minHeight: 180, maxHeight: 120})).to.be.ok()
      })

      it('Scale to minWidth but respect maxHeight', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(160)
          expect(img.height).to.be(120)
          done()
        }, {minWidth: 240, maxHeight: 120})).to.be.ok()
      })

      it('Scale to minHeight but respect maxWidth', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(160)
          expect(img.height).to.be(120)
          done()
        }, {minHeight: 180, maxWidth: 160})).to.be.ok()
      })

      it('Scale up with the given pixelRatio', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(320)
          expect(img.height).to.be(240)
          expect(img.style.width).to.be('160px')
          expect(img.style.height).to.be('120px')
          done()
        }, {minWidth: 160, canvas: true, pixelRatio: 2})).to.be.ok()
      })

      it('Scale down with the given pixelRatio', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(80)
          expect(img.height).to.be(60)
          expect(img.style.width).to.be('40px')
          expect(img.style.height).to.be('30px')
          done()
        }, {maxWidth: 40, canvas: true, pixelRatio: 2})).to.be.ok()
      })

      it('Scale down with the given downsamplingRatio', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(20)
          expect(img.height).to.be(15)
          done()
        }, {maxWidth: 20, canvas: true, downsamplingRatio: 0.5})).to.be.ok()
      })

      it('Ignore max settings if image dimensions are smaller', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(80)
          expect(img.height).to.be(60)
          done()
        }, {maxWidth: 160, maxHeight: 120})).to.be.ok()
      })

      it('Ignore min settings if image dimensions are larger', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(80)
          expect(img.height).to.be(60)
          done()
        }, {minWidth: 40, minHeight: 30})).to.be.ok()
      })
    })

    describe('contain', function () {
      it('Scale up to contain image in max dimensions', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(160)
          expect(img.height).to.be(120)
          done()
        }, {maxWidth: 160, maxHeight: 160, contain: true})).to.be.ok()
      })

      it('Scale down to contain image in max dimensions', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(40)
          expect(img.height).to.be(30)
          done()
        }, {maxWidth: 40, maxHeight: 40, contain: true})).to.be.ok()
      })
    })

    describe('cover', function () {
      it('Scale up to cover max dimensions with image dimensions', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(160)
          expect(img.height).to.be(120)
          done()
        }, {maxWidth: 120, maxHeight: 120, cover: true})).to.be.ok()
      })

      it('Scale down to cover max dimensions with image dimensions', function (done) {
        expect(loadImage(blobGIF, function (img) {
          expect(img.width).to.be(40)
          expect(img.height).to.be(30)
          done()
        }, {maxWidth: 30, maxHeight: 30, cover: true})).to.be.ok()
      })
    })
  })

  describe('Cropping', function () {
    it('Crop to same values for maxWidth and maxHeight', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(40)
        expect(img.height).to.be(40)
        done()
      }, {maxWidth: 40, maxHeight: 40, crop: true})).to.be.ok()
    })

    it('Crop to different values for maxWidth and maxHeight', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(40)
        expect(img.height).to.be(60)
        done()
      }, {maxWidth: 40, maxHeight: 60, crop: true})).to.be.ok()
    })

    it('Crop using the given sourceWidth and sourceHeight dimensions', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(40)
        expect(img.height).to.be(40)
        done()
      }, {sourceWidth: 40, sourceHeight: 40, crop: true})).to.be.ok()
    })

    it('Crop using the given left and top coordinates', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(40)
        expect(img.height).to.be(20)
        done()
      }, {left: 40, top: 40, crop: true})).to.be.ok()
    })

    it('Crop using the given right and bottom coordinates', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(40)
        expect(img.height).to.be(20)
        done()
      }, {right: 40, bottom: 40, crop: true})).to.be.ok()
    })

    it('Crop using the given 2:1 aspectRatio', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(40)
        done()
      }, {aspectRatio: 2})).to.be.ok()
    })

    it('Crop using the given 2:3 aspectRatio', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(40)
        expect(img.height).to.be(60)
        done()
      }, {aspectRatio: 2 / 3})).to.be.ok()
    })

    it('Crop using maxWidth/maxHeight with the given pixelRatio', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(80)
        expect(img.style.width).to.be('40px')
        expect(img.style.height).to.be('40px')
        done()
      }, {maxWidth: 40, maxHeight: 40, crop: true, pixelRatio: 2})).to.be.ok()
    })

    it('Crop using sourceWidth/sourceHeight with the given pixelRatio', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(80)
        expect(img.style.width).to.be('40px')
        expect(img.style.height).to.be('40px')
        done()
      }, {sourceWidth: 40, sourceHeight: 40, crop: true, pixelRatio: 2})).to.be.ok()
    })
  })

  describe('Orientation', function () {
    it('Should keep the orientation', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(60)
        done()
      }, {orientation: 1})).to.be.ok()
    })

    it('Should rotate left', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(60)
        expect(img.height).to.be(80)
        done()
      }, {orientation: 8})).to.be.ok()
    })

    it('Should rotate right', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(60)
        expect(img.height).to.be(80)
        done()
      }, {orientation: 6})).to.be.ok()
    })

    it('Should adjust constraints to new coordinates', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(60)
        expect(img.height).to.be(80)
        done()
      }, {orientation: 6, maxWidth: 60, maxHeight: 80})).to.be.ok()
    })

    it('Should adjust left and top to new coordinates', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(30)
        expect(img.height).to.be(60)
        done()
      }, {orientation: 5, left: 30, top: 20})).to.be.ok()
    })

    it('Should adjust right and bottom to new coordinates', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(30)
        expect(img.height).to.be(60)
        done()
      }, {orientation: 5, right: 30, bottom: 20})).to.be.ok()
    })

    it('Should adjust left and bottom to new coordinates', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(30)
        expect(img.height).to.be(60)
        done()
      }, {orientation: 7, left: 30, bottom: 20})).to.be.ok()
    })

    it('Should adjust right and top to new coordinates', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(30)
        expect(img.height).to.be(60)
        done()
      }, {orientation: 7, right: 30, top: 20})).to.be.ok()
    })

    it('Should rotate left with the given pixelRatio', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(120)
        expect(img.height).to.be(160)
        expect(img.style.width).to.be('60px')
        expect(img.style.height).to.be('80px')
        done()
      }, {orientation: 8, pixelRatio: 2})).to.be.ok()
    })

    it('Should rotate right with the given pixelRatio', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(120)
        expect(img.height).to.be(160)
        expect(img.style.width).to.be('60px')
        expect(img.style.height).to.be('80px')
        done()
      }, {orientation: 6, pixelRatio: 2})).to.be.ok()
    })

    it('Should ignore too small orientation value', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(60)
        done()
      }, {orientation: -1})).to.be.ok()
    })

    it('Should ignore too large orientation value', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.width).to.be(80)
        expect(img.height).to.be(60)
        done()
      }, {orientation: 9})).to.be.ok()
    })
  })

  describe('Canvas', function () {
    it('Return img element to callback if canvas is not true', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.getContext).to.not.be.ok()
        expect(img.nodeName.toLowerCase()).to.be('img')
        done()
      })).to.be.ok()
    })

    it('Return canvas element to callback if canvas is true', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.getContext).to.be.ok()
        expect(img.nodeName.toLowerCase()).to.be('canvas')
        done()
      }, {canvas: true})).to.be.ok()
    })

    it('Return scaled canvas element to callback', function (done) {
      expect(loadImage(blobGIF, function (img) {
        expect(img.getContext).to.be.ok()
        expect(img.nodeName.toLowerCase()).to.be('canvas')
        expect(img.width).to.be(40)
        expect(img.height).to.be(30)
        done()
      }, {canvas: true, maxWidth: 40})).to.be.ok()
    })

    it('Accept a canvas element as parameter for loadImage.scale', function (done) {
      expect(loadImage(blobGIF, function (img) {
        img = loadImage.scale(img, {
          maxWidth: 40
        })
        expect(img.getContext).to.be.ok()
        expect(img.nodeName.toLowerCase()).to.be('canvas')
        expect(img.width).to.be(40)
        expect(img.height).to.be(30)
        done()
      }, {canvas: true})).to.be.ok()
    })
  })

  describe('Metadata', function () {
    it('Should parse Exif information', function (done) {
      loadImage.parseMetaData(blobJPEG, function (data) {
        expect(data.exif).to.be.ok()
        expect(data.exif.get('Orientation')).to.be(6)
        done()
      })
    })

    it('Should parse the complete image head', function (done) {
      loadImage.parseMetaData(blobJPEG, function (data) {
        expect(data.imageHead).to.be.ok()
        loadImage.parseMetaData(
          createBlob(data.imageHead, 'image/jpeg'),
          function (data) {
            expect(data.exif).to.be.ok()
            expect(data.exif.get('Orientation')).to.be(6)
            done()
          }
        )
      })
    })
  })
}(
  this.expect,
  this.loadImage
))
