// Compiled by ClojureScript 1.8.40 {}
goog.provide('reagent.dom');
goog.require('cljs.core');
goog.require('cljsjs.react.dom');
goog.require('reagent.impl.util');
goog.require('reagent.impl.template');
goog.require('reagent.debug');
goog.require('reagent.interop');
if(typeof reagent.dom.dom !== 'undefined'){
} else {
reagent.dom.dom = (function (){var or__33763__auto__ = (function (){var and__33751__auto__ = typeof ReactDOM !== 'undefined';
if(and__33751__auto__){
return ReactDOM;
} else {
return and__33751__auto__;
}
})();
if(cljs.core.truth_(or__33763__auto__)){
return or__33763__auto__;
} else {
var and__33751__auto__ = typeof require !== 'undefined';
if(and__33751__auto__){
return require("react-dom");
} else {
return and__33751__auto__;
}
}
})();
}
if(cljs.core.truth_(reagent.dom.dom)){
} else {
throw (new Error([cljs.core.str("Assert failed: "),cljs.core.str("Could not find ReactDOM"),cljs.core.str("\n"),cljs.core.str("dom")].join('')));
}
if(typeof reagent.dom.roots !== 'undefined'){
} else {
reagent.dom.roots = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
reagent.dom.unmount_comp = (function reagent$dom$unmount_comp(container){
cljs.core.swap_BANG_.call(null,reagent.dom.roots,cljs.core.dissoc,container);

return (reagent.dom.dom["unmountComponentAtNode"])(container);
});
reagent.dom.render_comp = (function reagent$dom$render_comp(comp,container,callback){
var _STAR_always_update_STAR_35472 = reagent.impl.util._STAR_always_update_STAR_;
reagent.impl.util._STAR_always_update_STAR_ = true;

try{return (reagent.dom.dom["render"])(comp.call(null),container,((function (_STAR_always_update_STAR_35472){
return (function (){
var _STAR_always_update_STAR_35473 = reagent.impl.util._STAR_always_update_STAR_;
reagent.impl.util._STAR_always_update_STAR_ = false;

try{cljs.core.swap_BANG_.call(null,reagent.dom.roots,cljs.core.assoc,container,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [comp,container], null));

if(cljs.core.some_QMARK_.call(null,callback)){
return callback.call(null);
} else {
return null;
}
}finally {reagent.impl.util._STAR_always_update_STAR_ = _STAR_always_update_STAR_35473;
}});})(_STAR_always_update_STAR_35472))
);
}finally {reagent.impl.util._STAR_always_update_STAR_ = _STAR_always_update_STAR_35472;
}});
reagent.dom.re_render_component = (function reagent$dom$re_render_component(comp,container){
return reagent.dom.render_comp.call(null,comp,container,null);
});
/**
 * Render a Reagent component into the DOM. The first argument may be
 *   either a vector (using Reagent's Hiccup syntax), or a React element. The second argument should be a DOM node.
 * 
 *   Optionally takes a callback that is called when the component is in place.
 * 
 *   Returns the mounted component instance.
 */
reagent.dom.render = (function reagent$dom$render(var_args){
var args35474 = [];
var len__34832__auto___35477 = arguments.length;
var i__34833__auto___35478 = (0);
while(true){
if((i__34833__auto___35478 < len__34832__auto___35477)){
args35474.push((arguments[i__34833__auto___35478]));

var G__35479 = (i__34833__auto___35478 + (1));
i__34833__auto___35478 = G__35479;
continue;
} else {
}
break;
}

var G__35476 = args35474.length;
switch (G__35476) {
case 2:
return reagent.dom.render.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return reagent.dom.render.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error([cljs.core.str("Invalid arity: "),cljs.core.str(args35474.length)].join('')));

}
});

reagent.dom.render.cljs$core$IFn$_invoke$arity$2 = (function (comp,container){
return reagent.dom.render.call(null,comp,container,null);
});

reagent.dom.render.cljs$core$IFn$_invoke$arity$3 = (function (comp,container,callback){
var f = (function (){
return reagent.impl.template.as_element.call(null,((cljs.core.fn_QMARK_.call(null,comp))?comp.call(null):comp));
});
return reagent.dom.render_comp.call(null,f,container,callback);
});

reagent.dom.render.cljs$lang$maxFixedArity = 3;
reagent.dom.unmount_component_at_node = (function reagent$dom$unmount_component_at_node(container){
return reagent.dom.unmount_comp.call(null,container);
});
/**
 * Returns the root DOM node of a mounted component.
 */
reagent.dom.dom_node = (function reagent$dom$dom_node(this$){
return (reagent.dom.dom["findDOMNode"])(this$);
});
reagent.impl.template.find_dom_node = reagent.dom.dom_node;
/**
 * Force re-rendering of all mounted Reagent components. This is
 *   probably only useful in a development environment, when you want to
 *   update components in response to some dynamic changes to code.
 * 
 *   Note that force-update-all may not update root components. This
 *   happens if a component 'foo' is mounted with `(render [foo])` (since
 *   functions are passed by value, and not by reference, in
 *   ClojureScript). To get around this you'll have to introduce a layer
 *   of indirection, for example by using `(render [#'foo])` instead.
 */
reagent.dom.force_update_all = (function reagent$dom$force_update_all(){
var seq__35485_35489 = cljs.core.seq.call(null,cljs.core.vals.call(null,cljs.core.deref.call(null,reagent.dom.roots)));
var chunk__35486_35490 = null;
var count__35487_35491 = (0);
var i__35488_35492 = (0);
while(true){
if((i__35488_35492 < count__35487_35491)){
var v_35493 = cljs.core._nth.call(null,chunk__35486_35490,i__35488_35492);
cljs.core.apply.call(null,reagent.dom.re_render_component,v_35493);

var G__35494 = seq__35485_35489;
var G__35495 = chunk__35486_35490;
var G__35496 = count__35487_35491;
var G__35497 = (i__35488_35492 + (1));
seq__35485_35489 = G__35494;
chunk__35486_35490 = G__35495;
count__35487_35491 = G__35496;
i__35488_35492 = G__35497;
continue;
} else {
var temp__4657__auto___35498 = cljs.core.seq.call(null,seq__35485_35489);
if(temp__4657__auto___35498){
var seq__35485_35499__$1 = temp__4657__auto___35498;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__35485_35499__$1)){
var c__34574__auto___35500 = cljs.core.chunk_first.call(null,seq__35485_35499__$1);
var G__35501 = cljs.core.chunk_rest.call(null,seq__35485_35499__$1);
var G__35502 = c__34574__auto___35500;
var G__35503 = cljs.core.count.call(null,c__34574__auto___35500);
var G__35504 = (0);
seq__35485_35489 = G__35501;
chunk__35486_35490 = G__35502;
count__35487_35491 = G__35503;
i__35488_35492 = G__35504;
continue;
} else {
var v_35505 = cljs.core.first.call(null,seq__35485_35499__$1);
cljs.core.apply.call(null,reagent.dom.re_render_component,v_35505);

var G__35506 = cljs.core.next.call(null,seq__35485_35499__$1);
var G__35507 = null;
var G__35508 = (0);
var G__35509 = (0);
seq__35485_35489 = G__35506;
chunk__35486_35490 = G__35507;
count__35487_35491 = G__35508;
i__35488_35492 = G__35509;
continue;
}
} else {
}
}
break;
}

return "Updated";
});

//# sourceMappingURL=dom.js.map