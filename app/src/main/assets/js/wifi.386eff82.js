(function(t){function e(e){for(var i,r,s=e[0],c=e[1],l=e[2],f=0,p=[];f<s.length;f++)r=s[f],Object.prototype.hasOwnProperty.call(o,r)&&o[r]&&p.push(o[r][0]),o[r]=0;for(i in c)Object.prototype.hasOwnProperty.call(c,i)&&(t[i]=c[i]);u&&u(e);while(p.length)p.shift()();return a.push.apply(a,l||[]),n()}function n(){for(var t,e=0;e<a.length;e++){for(var n=a[e],i=!0,s=1;s<n.length;s++){var c=n[s];0!==o[c]&&(i=!1)}i&&(a.splice(e--,1),t=r(r.s=n[0]))}return t}var i={},o={wifi:0},a=[];function r(e){if(i[e])return i[e].exports;var n=i[e]={i:e,l:!1,exports:{}};return t[e].call(n.exports,n,n.exports,r),n.l=!0,n.exports}r.m=t,r.c=i,r.d=function(t,e,n){r.o(t,e)||Object.defineProperty(t,e,{enumerable:!0,get:n})},r.r=function(t){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(t,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(t,"__esModule",{value:!0})},r.t=function(t,e){if(1&e&&(t=r(t)),8&e)return t;if(4&e&&"object"===typeof t&&t&&t.__esModule)return t;var n=Object.create(null);if(r.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:t}),2&e&&"string"!=typeof t)for(var i in t)r.d(n,i,function(e){return t[e]}.bind(null,i));return n},r.n=function(t){var e=t&&t.__esModule?function(){return t["default"]}:function(){return t};return r.d(e,"a",e),e},r.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},r.p="/";var s=window["webpackJsonp"]=window["webpackJsonp"]||[],c=s.push.bind(s);s.push=e,s=s.slice();for(var l=0;l<s.length;l++)e(s[l]);var u=c;a.push([1,"chunk-vendors"]),n()})({1:function(t,e,n){t.exports=n("7f22")},"402c":function(t,e,n){"use strict";n("5363");var i=n("2b0e"),o=n("f309");i["a"].use(o["a"]),e["a"]=new o["a"]({icons:{iconfont:"mdi"}})},4666:function(t,e,n){"use strict";n.d(e,"b",function(){return a}),n.d(e,"a",function(){return r});var i=n("bc3a"),o=n.n(i);async function a(){const{data:t}=await o.a.get(`http://${location.host}/api/v1/is-authorized`);return t.isUserAuthorized}async function r(){const{data:t}=await o.a.get(`http://${location.host}/api/v1/is-required-login`);return t.isRequiredLogin}},"7f22":function(t,e,n){"use strict";n.r(e);var i=n("2b0e"),o=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("v-app",[n("v-content",[t.displayable?n("WifiConfig"):t._e()],1)],1)},a=[],r=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("v-container",[n("v-layout",{attrs:{wrap:""}},[n("v-flex",{attrs:{xs12:""}},[n("v-list",[n("v-subheader",{staticClass:"indigo--text display-1"},[t._v("Wifi list")]),n("v-list-item-group",{attrs:{color:"primary"},model:{value:t.selectedWifiIndex,callback:function(e){t.selectedWifiIndex=e},expression:"selectedWifiIndex"}},t._l(t.wifis,function(e,i){return n("v-list-item",{key:i},[n("v-list-item-content",[n("v-list-item-title",{domProps:{textContent:t._s(e)}})],1)],1)}),1)],1)],1),null!==t.selectedWifiIndex?n("v-flex",{attrs:{xs12:"","mt-12":""}},[n("h4",[t._v("Selected network: "+t._s(t.wifis[t.selectedWifiIndex]))]),n("v-text-field",{attrs:{type:"password",label:"Enter password"},model:{value:t.password,callback:function(e){t.password=e},expression:"password"}})],1):t._e(),n("v-flex",{attrs:{xs12:"","mt-6":""}},[null!==t.selectedWifiIndex?n("v-btn",{attrs:{color:"primary mr-2",large:""},on:{click:t.connectToNetwork}},[t._v("Connect")]):t._e(),n("v-btn",{attrs:{color:"success",large:""},on:{click:t.getAvailableWifis}},[t._v("Scan")])],1),n("v-flex",{attrs:{xs12:""}},[n("h3",{class:{"indigo--text":t.isConnectionOk,"red--text":!t.isConnectionOk}},[t._v(t._s(t.networkConnectionResult))])])],1)],1)},s=[],c=n("bc3a"),l=n.n(c),u={data(){return{selectedWifiIndex:null,wifis:[],password:"",networkConnectionResult:"",isConnectionOk:!1}},created(){this.getAvailableWifis()},methods:{async getAvailableWifis(){try{const e=await l.a.get(`http://${location.host}/api/v1/wifi`);this.wifis=e.data}catch(t){console.error(t)}},async connectToNetwork(){try{this.isConnectionOk=!0,this.networkConnectionResult="Attempting connection...";const e={ssid:this.wifis[this.selectedWifiIndex],passphrase:this.password},n=await l.a.post(`http://${location.host}/api/v1/wifi/connect`,e);this.isConnectionOk=n.data.result,this.networkConnectionResult=this.isConnectionOk?"Success, app will restart shortly":"Connection failed, check your password"}catch(t){console.error(t)}}}},f=u,p=n("2877"),d=n("6544"),h=n.n(d),v=n("8336"),b=n("a523"),w=n("0e8f"),y=n("a722"),x=n("8860"),m=n("da13"),g=n("5d23"),k=n("1baa"),_=n("e0c7"),O=n("8654"),C=Object(p["a"])(f,r,s,!1,null,null,null),W=C.exports;h()(C,{VBtn:v["a"],VContainer:b["a"],VFlex:w["a"],VLayout:y["a"],VList:x["a"],VListItem:m["a"],VListItemContent:g["a"],VListItemGroup:k["a"],VListItemTitle:g["b"],VSubheader:_["a"],VTextField:O["a"]});var j=n("4666"),V={name:"App",data(){return{displayable:!1}},components:{WifiConfig:W},async beforeCreate(){try{const e=await Object(j["b"])(),n=await Object(j["a"])();this.displayable=e&&!n,this.displayable||(window.location.href=`http://${location.host}/authorize`)}catch(t){console.error(t)}}},I=V,S=n("7496"),P=n("a75b"),$=Object(p["a"])(I,o,a,!1,null,null,null),A=$.exports;h()($,{VApp:S["a"],VContent:P["a"]});var L=n("402c");i["a"].config.productionTip=!1,new i["a"]({vuetify:L["a"],render:t=>t(A)}).$mount("#app")}});
//# sourceMappingURL=wifi.386eff82.js.map