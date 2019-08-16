(function(e){function t(t){for(var s,n,l=t[0],o=t[1],c=t[2],p=0,m=[];p<l.length;p++)n=l[p],r[n]&&m.push(r[n][0]),r[n]=0;for(s in o)Object.prototype.hasOwnProperty.call(o,s)&&(e[s]=o[s]);u&&u(t);while(m.length)m.shift()();return a.push.apply(a,c||[]),i()}function i(){for(var e,t=0;t<a.length;t++){for(var i=a[t],s=!0,l=1;l<i.length;l++){var o=i[l];0!==r[o]&&(s=!1)}s&&(a.splice(t--,1),e=n(n.s=i[0]))}return e}var s={},r={index:0},a=[];function n(t){if(s[t])return s[t].exports;var i=s[t]={i:t,l:!1,exports:{}};return e[t].call(i.exports,i,i.exports,n),i.l=!0,i.exports}n.m=e,n.c=s,n.d=function(e,t,i){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:i})},n.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t)return e;if(4&t&&"object"===typeof e&&e&&e.__esModule)return e;var i=Object.create(null);if(n.r(i),Object.defineProperty(i,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var s in e)n.d(i,s,function(t){return e[t]}.bind(null,s));return i},n.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="/";var l=window["webpackJsonp"]=window["webpackJsonp"]||[],o=l.push.bind(l);l.push=t,l=l.slice();for(var c=0;c<l.length;c++)t(l[c]);var u=o;a.push([0,"chunk-vendors"]),i()})({0:function(e,t,i){e.exports=i("df31")},"402c":function(e,t,i){"use strict";var s=i("2b0e"),r=i("f309");s["a"].use(r["a"]),t["a"]=new r["a"]({icons:{iconfont:"mdi"}})},4666:function(e,t,i){"use strict";i.d(t,"a",function(){return n});i("96cf");var s=i("3b8d"),r=i("bc3a"),a=i.n(r);function n(){return l.apply(this,arguments)}function l(){return l=Object(s["a"])(regeneratorRuntime.mark(function e(){var t,i;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,a.a.get("http://".concat(location.host,"/api/v1/is-authorized"));case 2:return t=e.sent,i=t.data,e.abrupt("return",i.isUserAuthorized);case 5:case"end":return e.stop()}},e)})),l.apply(this,arguments)}},df31:function(e,t,i){"use strict";i.r(t);i("cadf"),i("551c"),i("f751"),i("097d");var s=i("2b0e"),r=function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("v-app",[i("v-content",[e.isAuthorized?i("AppPreference"):e._e()],1)],1)},a=[],n=(i("96cf"),i("3b8d")),l=function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("v-container",[i("v-form",{ref:"form",model:{value:e.formValid,callback:function(t){e.formValid=t},expression:"formValid"}},[i("v-layout",{attrs:{wrap:""}},[i("h3",{staticClass:"indigo--text"},[e._v("Instagram configurations")]),i("v-flex",{attrs:{xs12:""}},[i("v-text-field",{attrs:{error:!e.isInstagramSourceValid,"error-messages":e.instagramSourceUrlErrorMsg,label:"Instagram source URL",hint:"Example: https://www.instagram.com/adidas",required:""},on:{focusout:e.validateInstagramSource,focus:e.setBaseInstagramUrl},model:{value:e.instagramSourceUrl,callback:function(t){e.instagramSourceUrl=t},expression:"instagramSourceUrl"}})],1),i("h3",{staticClass:"indigo--text"},[e._v("Presentation configurations")]),i("v-flex",{attrs:{xs12:""}},[i("v-text-field",{attrs:{type:"number",rules:e.numberOfPostRules,label:"Number of posts to display (Use 0 for all posts)",required:""},model:{value:e.numberOfPostsToDisplay,callback:function(t){e.numberOfPostsToDisplay=t},expression:"numberOfPostsToDisplay"}})],1),i("v-flex",[i("v-text-field",{attrs:{label:"Excluded Hashtags, separated by commas",hint:"Example: #new, #cool, #abc"},model:{value:e.excludedHashtags,callback:function(t){e.excludedHashtags=t},expression:"excludedHashtags"}})],1),i("v-flex",{attrs:{xs12:""}},[i("h3",{staticClass:"indigo--text"},[e._v("Size configurations")])]),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{staticClass:"pr-3",attrs:{type:"number",rules:e.widthRules,label:"Profile image width",required:""},model:{value:e.profilePicWidth,callback:function(t){e.profilePicWidth=t},expression:"profilePicWidth"}})],1),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{attrs:{type:"number",rules:e.heightRules,label:"Profile image height",required:""},model:{value:e.profilePicHeight,callback:function(t){e.profilePicHeight=t},expression:"profilePicHeight"}})],1),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{staticClass:"pr-3",attrs:{type:"number",rules:e.widthRules,label:"Main image width",required:""},model:{value:e.imgMainWidth,callback:function(t){e.imgMainWidth=t},expression:"imgMainWidth"}})],1),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{attrs:{type:"number",rules:e.heightRules,label:"Main image height",required:""},model:{value:e.imgMainHeight,callback:function(t){e.imgMainHeight=t},expression:"imgMainHeight"}})],1),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{staticClass:"pr-3",attrs:{type:"number",rules:e.sizeRules,label:"Username text size",required:""},model:{value:e.usernameTextSize,callback:function(t){e.usernameTextSize=t},expression:"usernameTextSize"}})],1),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{attrs:{type:"number",rules:e.sizeRules,label:"Likes text size",required:""},model:{value:e.likeTextSize,callback:function(t){e.likeTextSize=t},expression:"likeTextSize"}})],1),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{staticClass:"pr-3",attrs:{type:"number",rules:e.sizeRules,label:"Comments text size",required:""},model:{value:e.commentTextSize,callback:function(t){e.commentTextSize=t},expression:"commentTextSize"}})],1),i("v-flex",{attrs:{xs6:""}},[i("v-text-field",{attrs:{type:"number",rules:e.sizeRules,label:"Caption text size",required:""},model:{value:e.captionTextSize,callback:function(t){e.captionTextSize=t},expression:"captionTextSize"}})],1),i("v-flex",{attrs:{xs6:""}}),i("v-flex",{attrs:{xs12:""}},[i("v-text-field",{attrs:{type:"number",rules:e.intervalRules,label:"Each image is shown for: (in ms, 1000ms = 1 second)"},model:{value:e.presentInterval,callback:function(t){e.presentInterval=t},expression:"presentInterval"}})],1),i("h3",{staticClass:"indigo--text"},[e._v("Visibility configurations")]),i("v-flex",{attrs:{xs12:""}},[i("v-switch",{attrs:{label:e.profilePicMsg},model:{value:e.isProfilePicDisplayed,callback:function(t){e.isProfilePicDisplayed=t},expression:"isProfilePicDisplayed"}})],1),i("v-flex",{attrs:{xs12:""}},[i("v-switch",{attrs:{label:e.usernameMsg},model:{value:e.isUsernameDisplayed,callback:function(t){e.isUsernameDisplayed=t},expression:"isUsernameDisplayed"}})],1),i("v-flex",{attrs:{xs12:""}},[i("v-switch",{attrs:{label:e.likeMsg},model:{value:e.isLikesDisplayed,callback:function(t){e.isLikesDisplayed=t},expression:"isLikesDisplayed"}})],1),i("v-flex",{attrs:{xs12:""}},[i("v-switch",{attrs:{label:e.commentMsg},model:{value:e.isCommentsDisplayed,callback:function(t){e.isCommentsDisplayed=t},expression:"isCommentsDisplayed"}})],1),i("v-flex",{attrs:{xs12:""}},[i("v-switch",{attrs:{label:e.captionMsg},model:{value:e.isCaptionDisplayed,callback:function(t){e.isCaptionDisplayed=t},expression:"isCaptionDisplayed"}})],1),e.isLicenseValid?e._e():[i("v-flex",{attrs:{xs12:""}},[i("h3",{staticClass:"indigo--text"},[e._v("License configurations (Your key id is "+e._s(e.licenseKeyId)+")")])]),i("v-flex",{attrs:{xs9:""}},[i("v-text-field",{staticClass:"pr-3",attrs:{label:"Enter license key"},model:{value:e.licenseKey,callback:function(t){e.licenseKey=t},expression:"licenseKey"}})],1),i("v-flex",{attrs:{xs3:""}},[i("v-layout",{attrs:{"align-center":"","fill-height":""}},[i("v-btn",{staticClass:"white--text",attrs:{color:"green",disabled:e.isLicenseSubmitted&&e.isLicenseValid},on:{click:e.validateKey}},[e._v("Submit\n            ")])],1)],1)],i("v-flex",{attrs:{xs12:""}},[e.isLicenseSubmitted&&e.isLicenseValid?i("h6",{staticClass:"green--text"},[e._v('\n          Success! please click "Save" to refresh the application')]):e.isLicenseSubmitted&&!e.isLicenseValid?i("h6",{staticClass:"red--text"},[e._v("\n          Invalid license key")]):e._e()]),i("v-flex",{attrs:{xs12:"","mt-12":""}},[i("v-layout",{attrs:{"justify-center":""}},[i("v-btn",{staticClass:"mr-2",attrs:{color:"primary",disabled:!e.formValid,large:""},on:{click:e.saveAppPreference}},[e._v("Save\n          ")]),i("v-btn",{attrs:{color:"error",large:"",dark:""},on:{click:e.getAppPreferences}},[e._v("Reset")])],1)],1)],2)],1)],1)},o=[],c=(i("28a5"),i("f559"),i("bc3a")),u=i.n(c),p={data:function(){return{instagramSourceUrl:"",numberOfPostsToDisplay:0,excludedHashtags:"",isProfilePicDisplayed:!1,isUsernameDisplayed:!1,isLikesDisplayed:!1,isCommentsDisplayed:!1,isCaptionDisplayed:!1,isInstagramSourceValid:!1,formValid:!1,licenseKey:"",licenseKeyId:"",isLicenseValid:!1,isLicenseSubmitted:!1,instagramSourceUrlErrorMsg:"Please specify a valid Instagram user URL",profilePicWidth:0,profilePicHeight:0,usernameTextSize:0,imgMainWidth:0,imgMainHeight:0,likeTextSize:0,commentTextSize:0,captionTextSize:0,presentInterval:0,numberOfPostRules:[function(e){return/^-{0,1}\d+$/.test(e)||"Number of post must be an integer"},function(e){return parseInt(e,10)>=0||"Minimum number is 0"}],widthRules:[function(e){return/^-{0,1}\d+$/.test(e)||"Width must be an integer"},function(e){return parseInt(e,10)>=0||"Minimum width is 0"}],heightRules:[function(e){return/^-{0,1}\d+$/.test(e)||"Height must be an integer"},function(e){return parseInt(e,10)>=0||"Minimum height is 0"}],sizeRules:[function(e){return/^-{0,1}\d+$/.test(e)||"Size must be an integer"},function(e){return parseInt(e,10)>=0||"Minimum size is 0"}],intervalRules:[function(e){return/^-{0,1}\d+$/.test(e)||"Interval must be an integer"},function(e){return parseInt(e,10)>=5e3||"Minimum interval is 5000"}]}},created:function(){this.getAppPreferences(),this.getLicenseKeyId(),this.isValidated()},methods:{getAppPreferences:function(){var e=Object(n["a"])(regeneratorRuntime.mark(function e(){var t,i;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.prev=0,e.next=3,u.a.get("http://".concat(location.host,"/api/v1/preference"));case 3:t=e.sent,i=t.data,this.instagramSourceUrl=i.instagramSourceUrl,this.numberOfPostsToDisplay=i.numberOfPostsToDisplay,this.excludedHashtags=i.excludedHashtags,this.isProfilePicDisplayed=i.isProfilePicDisplayed,this.isUsernameDisplayed=i.isUsernameDisplayed,this.isLikesDisplayed=i.isLikesDisplayed,this.isCommentsDisplayed=i.isCommentsDisplayed,this.isCaptionDisplayed=i.isCaptionDisplayed,this.profilePicWidth=i.profilePicWidth,this.profilePicHeight=i.profilePicHeight,this.imgMainWidth=i.imgMainWidth,this.imgMainHeight=i.imgMainHeight,this.usernameTextSize=i.usernameTextSize,this.likeTextSize=i.likeTextSize,this.commentTextSize=i.commentTextSize,this.captionTextSize=i.captionTextSize,this.presentInterval=i.presentInterval,this.validateInstagramSource(),e.next=28;break;case 25:e.prev=25,e.t0=e["catch"](0),console.error(e.t0);case 28:case"end":return e.stop()}},e,this,[[0,25]])}));function t(){return e.apply(this,arguments)}return t}(),saveAppPreference:function(){var e=Object(n["a"])(regeneratorRuntime.mark(function e(){var t;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.prev=0,t={instagramSourceUrl:this.instagramSourceUrl,numberOfPostsToDisplay:this.numberOfPostsToDisplay,excludedHashtags:this.excludedHashtags,isProfilePicDisplayed:this.isProfilePicDisplayed,isUsernameDisplayed:this.isUsernameDisplayed,isLikesDisplayed:this.isLikesDisplayed,isCommentsDisplayed:this.isCommentsDisplayed,isCaptionDisplayed:this.isCaptionDisplayed,profilePicWidth:this.profilePicWidth,profilePicHeight:this.profilePicHeight,usernameTextSize:this.usernameTextSize,imgMainWidth:this.imgMainWidth,imgMainHeight:this.imgMainHeight,likeTextSize:this.likeTextSize,commentTextSize:this.commentTextSize,captionTextSize:this.captionTextSize,presentInterval:this.presentInterval},e.next=4,u.a.post("http://".concat(location.host,"/api/v1/preference"),t);case 4:e.next=9;break;case 6:e.prev=6,e.t0=e["catch"](0),console.error(e.t0);case 9:case"end":return e.stop()}},e,this,[[0,6]])}));function t(){return e.apply(this,arguments)}return t}(),validateInstagramSource:function(){var e=Object(n["a"])(regeneratorRuntime.mark(function e(){return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:if(e.prev=0,(this.instagramSourceUrl.toLowerCase().startsWith("https://www.instagram.com/")||this.instagramSourceUrl.toLowerCase().startsWith("https://instagram.com/")||this.instagramSourceUrl.toLowerCase().startsWith("www.instagram.com/")||this.instagramSourceUrl.toLowerCase().startsWith("instagram.com/"))&&0!==this.instagramSourceUrl.split("instagram.com/")[1].trim().length){e.next=5;break}throw new Error;case 5:return e.next=7,u.a.get(this.instagramSourceUrl);case 7:this.instagramSourceUrlErrorMsg="",this.isInstagramSourceValid=!0;case 9:e.next=15;break;case 11:e.prev=11,e.t0=e["catch"](0),this.instagramSourceUrlErrorMsg="Please specify a valid Instagram user URL",this.isInstagramSourceValid=!1;case 15:case"end":return e.stop()}},e,this,[[0,11]])}));function t(){return e.apply(this,arguments)}return t}(),getLicenseKeyId:function(){var e=Object(n["a"])(regeneratorRuntime.mark(function e(){var t,i;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.prev=0,e.next=3,u.a.get("http://".concat(location.host,"/api/v1/license"));case 3:t=e.sent,i=t.data,this.licenseKeyId=i,e.next=11;break;case 8:e.prev=8,e.t0=e["catch"](0),console.warn(e.t0);case 11:case"end":return e.stop()}},e,this,[[0,8]])}));function t(){return e.apply(this,arguments)}return t}(),validateKey:function(){var e=Object(n["a"])(regeneratorRuntime.mark(function e(){var t;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.prev=0,t={licenseKey:this.licenseKey},e.next=4,u.a.post("http://".concat(location.host,"/api/v1/license/validate"),t);case 4:this.isLicenseValid=!0,e.next=11;break;case 7:e.prev=7,e.t0=e["catch"](0),console.warn(e.t0),this.isLicenseValid=!1;case 11:this.isLicenseSubmitted=!0;case 12:case"end":return e.stop()}},e,this,[[0,7]])}));function t(){return e.apply(this,arguments)}return t}(),isValidated:function(){var e=Object(n["a"])(regeneratorRuntime.mark(function e(){var t,i;return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:try{t=u.a.get("http://".concat(location.host,"/api/v1/license/is-validated")),i=t.data,this.isLicenseValid=i.validated}catch(s){console.warn(s),this.isLicenseValid=!0}case 1:case"end":return e.stop()}},e,this)}));function t(){return e.apply(this,arguments)}return t}(),setBaseInstagramUrl:function(){this.instagramSourceUrlErrorMsg="",this.instagramSourceUrl.startsWith("https://www.instagram.com/")||(this.instagramSourceUrl="https://www.instagram.com/")}},computed:{profilePicMsg:function(){return this.isProfilePicDisplayed?"Display user's profile picture":"Do not display user's profile picture"},usernameMsg:function(){return this.isUsernameDisplayed?"Display username":"Do not display username"},likeMsg:function(){return this.isLikesDisplayed?"Display number of likes":"Do not display number of likes"},commentMsg:function(){return this.isCommentsDisplayed?"Display number of comments":"Do not display number of comments"},captionMsg:function(){return this.isCaptionDisplayed?"Display post caption":"Do not display post caption"}}},m=p,d=i("2877"),f=i("6544"),h=i.n(f),g=i("8336"),v=i("a523"),x=i("0e8f"),b=i("4bd4"),y=i("a722"),w=i("b73d"),S=i("8654"),k=Object(d["a"])(m,l,o,!1,null,null,null),P=k.exports;h()(k,{VBtn:g["a"],VContainer:v["a"],VFlex:x["a"],VForm:b["a"],VLayout:y["a"],VSwitch:w["a"],VTextField:S["a"]});var D=i("4666"),z={name:"App",data:function(){return{isAuthorized:!1}},components:{AppPreference:P},beforeCreate:function(){var e=Object(n["a"])(regeneratorRuntime.mark(function e(){return regeneratorRuntime.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,Object(D["a"])();case 2:if(e.sent){e.next=6;break}window.location.href="http://".concat(location.host,"/login"),e.next=7;break;case 6:this.isAuthorized=!0;case 7:case"end":return e.stop()}},e,this)}));function t(){return e.apply(this,arguments)}return t}()},T=z,M=i("7496"),C=i("a75b"),U=Object(d["a"])(T,r,a,!1,null,null,null),L=U.exports;h()(U,{VApp:M["a"],VContent:C["a"]});var R=i("402c");s["a"].config.productionTip=!1,new s["a"]({vuetify:R["a"],render:function(e){return e(L)}}).$mount("#app")}});
//# sourceMappingURL=index.bf8033b2.js.map