/*==================================================
 *  Timeplot API
 *
 *  This file will load all the Javascript files
 *  necessary to make the standard timeplot work.
 *  It also detects the default locale.
 *
 *  Include this file in your HTML file as follows:
 *
 *    <script src="http://simile.mit.edu/timeplot/api/scripts/timeplot-api.js" type="text/javascript"></script>
 *
 *==================================================
 */
var packagePath = zk.ajaxURI('/web/js/timeplotz/ext/timeplot/', {desktop: this.desktop, au: true});
packagePath = packagePath.substr(0, packagePath.lastIndexOf("/") + 1);
(function() {
    // Load Timeplot if it's not already loaded (after SimileAjax and Timeline)
    var loadTimeplot = function() {

        if (typeof window.Timeplot != "undefined") {
            return;
        }
        
        window.Timeplot = {
            loaded:     false,
            params:     { bundle: true, autoCreate: true },
            namespace:  "http://simile.mit.edu/2007/06/timeplot#",
            importers:  {}
        };
        
        var locales = [ "en" ];

        var defaultClientLocales = ("language" in navigator ? navigator.language : navigator.browserLanguage).split(";");
        for (var l = 0; l < defaultClientLocales.length; l++) {
            var locale = defaultClientLocales[l];
            if (locale != "en") {
                var segments = locale.split("-");
                if (segments.length > 1 && segments[0] != "en") {
                    locales.push(segments[0]);
                }
                locales.push(locale);
            }
        }
        
		Timeplot.urlPrefix = packagePath;
        SimileAjax.includeCssFiles(document, "", [Timeplot.urlPrefix + "timeplot-bundle.css"]);
        Timeplot.loaded = true;
    };

	loadTimeplot();
})();
