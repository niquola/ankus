// Compiled by ClojureScript 1.8.40 {}
goog.provide('reagent.session');
goog.require('cljs.core');
goog.require('reagent.core');
reagent.session.state = reagent.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
/**
 * Get the key's value from the session, returns nil if it doesn't exist.
 */
reagent.session.get = (function reagent$session$get(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35913 = arguments.length;
var i__34833__auto___35914 = (0);
while(true){
if((i__34833__auto___35914 < len__34832__auto___35913)){
args__34839__auto__.push((arguments[i__34833__auto___35914]));

var G__35915 = (i__34833__auto___35914 + (1));
i__34833__auto___35914 = G__35915;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((1) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((1)),(0),null)):null);
return reagent.session.get.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__34840__auto__);
});

reagent.session.get.cljs$core$IFn$_invoke$arity$variadic = (function (k,p__35911){
var vec__35912 = p__35911;
var default$ = cljs.core.nth.call(null,vec__35912,(0),null);
return cljs.core.get.call(null,cljs.core.deref.call(null,reagent.session.state),k,default$);
});

reagent.session.get.cljs$lang$maxFixedArity = (1);

reagent.session.get.cljs$lang$applyTo = (function (seq35909){
var G__35910 = cljs.core.first.call(null,seq35909);
var seq35909__$1 = cljs.core.next.call(null,seq35909);
return reagent.session.get.cljs$core$IFn$_invoke$arity$variadic(G__35910,seq35909__$1);
});
reagent.session.put_BANG_ = (function reagent$session$put_BANG_(k,v){
return cljs.core.swap_BANG_.call(null,reagent.session.state,cljs.core.assoc,k,v);
});
/**
 * Gets the value at the path specified by the vector ks from the session,
 *   returns nil if it doesn't exist.
 */
reagent.session.get_in = (function reagent$session$get_in(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35920 = arguments.length;
var i__34833__auto___35921 = (0);
while(true){
if((i__34833__auto___35921 < len__34832__auto___35920)){
args__34839__auto__.push((arguments[i__34833__auto___35921]));

var G__35922 = (i__34833__auto___35921 + (1));
i__34833__auto___35921 = G__35922;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((1) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((1)),(0),null)):null);
return reagent.session.get_in.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__34840__auto__);
});

reagent.session.get_in.cljs$core$IFn$_invoke$arity$variadic = (function (ks,p__35918){
var vec__35919 = p__35918;
var default$ = cljs.core.nth.call(null,vec__35919,(0),null);
return cljs.core.get_in.call(null,cljs.core.deref.call(null,reagent.session.state),ks,default$);
});

reagent.session.get_in.cljs$lang$maxFixedArity = (1);

reagent.session.get_in.cljs$lang$applyTo = (function (seq35916){
var G__35917 = cljs.core.first.call(null,seq35916);
var seq35916__$1 = cljs.core.next.call(null,seq35916);
return reagent.session.get_in.cljs$core$IFn$_invoke$arity$variadic(G__35917,seq35916__$1);
});
/**
 * Replace the current session's value with the result of executing f with
 *   the current value and args.
 */
reagent.session.swap_BANG_ = (function reagent$session$swap_BANG_(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35925 = arguments.length;
var i__34833__auto___35926 = (0);
while(true){
if((i__34833__auto___35926 < len__34832__auto___35925)){
args__34839__auto__.push((arguments[i__34833__auto___35926]));

var G__35927 = (i__34833__auto___35926 + (1));
i__34833__auto___35926 = G__35927;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((1) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((1)),(0),null)):null);
return reagent.session.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__34840__auto__);
});

reagent.session.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (f,args){
return cljs.core.apply.call(null,cljs.core.swap_BANG_,reagent.session.state,f,args);
});

reagent.session.swap_BANG_.cljs$lang$maxFixedArity = (1);

reagent.session.swap_BANG_.cljs$lang$applyTo = (function (seq35923){
var G__35924 = cljs.core.first.call(null,seq35923);
var seq35923__$1 = cljs.core.next.call(null,seq35923);
return reagent.session.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__35924,seq35923__$1);
});
/**
 * Remove all data from the session and start over cleanly.
 */
reagent.session.clear_BANG_ = (function reagent$session$clear_BANG_(){
return cljs.core.reset_BANG_.call(null,reagent.session.state,cljs.core.PersistentArrayMap.EMPTY);
});
reagent.session.reset_BANG_ = (function reagent$session$reset_BANG_(m){
return cljs.core.reset_BANG_.call(null,reagent.session.state,m);
});
/**
 * Remove a key from the session
 */
reagent.session.remove_BANG_ = (function reagent$session$remove_BANG_(k){
return cljs.core.swap_BANG_.call(null,reagent.session.state,cljs.core.dissoc,k);
});
/**
 * Associates a value in the session, where ks is a
 * sequence of keys and v is the new value and returns
 * a new nested structure. If any levels do not exist,
 * hash-maps will be created.
 */
reagent.session.assoc_in_BANG_ = (function reagent$session$assoc_in_BANG_(ks,v){
return cljs.core.swap_BANG_.call(null,reagent.session.state,(function (p1__35928_SHARP_){
return cljs.core.assoc_in.call(null,p1__35928_SHARP_,ks,v);
}));
});
/**
 * Destructive get from the session. This returns the current value of the key
 *   and then removes it from the session.
 */
reagent.session.get_BANG_ = (function reagent$session$get_BANG_(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35933 = arguments.length;
var i__34833__auto___35934 = (0);
while(true){
if((i__34833__auto___35934 < len__34832__auto___35933)){
args__34839__auto__.push((arguments[i__34833__auto___35934]));

var G__35935 = (i__34833__auto___35934 + (1));
i__34833__auto___35934 = G__35935;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((1) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((1)),(0),null)):null);
return reagent.session.get_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__34840__auto__);
});

reagent.session.get_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (k,p__35931){
var vec__35932 = p__35931;
var default$ = cljs.core.nth.call(null,vec__35932,(0),null);
var cur = reagent.session.get.call(null,k,default$);
reagent.session.remove_BANG_.call(null,k);

return cur;
});

reagent.session.get_BANG_.cljs$lang$maxFixedArity = (1);

reagent.session.get_BANG_.cljs$lang$applyTo = (function (seq35929){
var G__35930 = cljs.core.first.call(null,seq35929);
var seq35929__$1 = cljs.core.next.call(null,seq35929);
return reagent.session.get_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__35930,seq35929__$1);
});
/**
 * Destructive get from the session. This returns the current value of the path
 *   specified by the vector ks and then removes it from the session.
 */
reagent.session.get_in_BANG_ = (function reagent$session$get_in_BANG_(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35940 = arguments.length;
var i__34833__auto___35941 = (0);
while(true){
if((i__34833__auto___35941 < len__34832__auto___35940)){
args__34839__auto__.push((arguments[i__34833__auto___35941]));

var G__35942 = (i__34833__auto___35941 + (1));
i__34833__auto___35941 = G__35942;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((1) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((1)),(0),null)):null);
return reagent.session.get_in_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__34840__auto__);
});

reagent.session.get_in_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ks,p__35938){
var vec__35939 = p__35938;
var default$ = cljs.core.nth.call(null,vec__35939,(0),null);
var cur = cljs.core.get_in.call(null,cljs.core.deref.call(null,reagent.session.state),ks,default$);
reagent.session.assoc_in_BANG_.call(null,ks,null);

return cur;
});

reagent.session.get_in_BANG_.cljs$lang$maxFixedArity = (1);

reagent.session.get_in_BANG_.cljs$lang$applyTo = (function (seq35936){
var G__35937 = cljs.core.first.call(null,seq35936);
var seq35936__$1 = cljs.core.next.call(null,seq35936);
return reagent.session.get_in_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__35937,seq35936__$1);
});
/**
 * Updates a value in session where k is a key and f
 * is the function that takes the old value along with any
 * supplied args and return the new value. If key is not
 * present it will be added.
 */
reagent.session.update_BANG_ = (function reagent$session$update_BANG_(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35947 = arguments.length;
var i__34833__auto___35948 = (0);
while(true){
if((i__34833__auto___35948 < len__34832__auto___35947)){
args__34839__auto__.push((arguments[i__34833__auto___35948]));

var G__35949 = (i__34833__auto___35948 + (1));
i__34833__auto___35948 = G__35949;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((2) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((2)),(0),null)):null);
return reagent.session.update_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__34840__auto__);
});

reagent.session.update_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (k,f,args){
return cljs.core.swap_BANG_.call(null,reagent.session.state,(function (p1__35943_SHARP_){
return cljs.core.apply.call(null,cljs.core.partial.call(null,cljs.core.update,p1__35943_SHARP_,k,f),args);
}));
});

reagent.session.update_BANG_.cljs$lang$maxFixedArity = (2);

reagent.session.update_BANG_.cljs$lang$applyTo = (function (seq35944){
var G__35945 = cljs.core.first.call(null,seq35944);
var seq35944__$1 = cljs.core.next.call(null,seq35944);
var G__35946 = cljs.core.first.call(null,seq35944__$1);
var seq35944__$2 = cljs.core.next.call(null,seq35944__$1);
return reagent.session.update_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__35945,G__35946,seq35944__$2);
});
/**
 * 'Updates a value in the session, where ks is a
 * sequence of keys and f is a function that will
 * take the old value along with any supplied args and return
 * the new value. If any levels do not exist, hash-maps
 * will be created.
 */
reagent.session.update_in_BANG_ = (function reagent$session$update_in_BANG_(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35954 = arguments.length;
var i__34833__auto___35955 = (0);
while(true){
if((i__34833__auto___35955 < len__34832__auto___35954)){
args__34839__auto__.push((arguments[i__34833__auto___35955]));

var G__35956 = (i__34833__auto___35955 + (1));
i__34833__auto___35955 = G__35956;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((2) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((2)),(0),null)):null);
return reagent.session.update_in_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__34840__auto__);
});

reagent.session.update_in_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ks,f,args){
return cljs.core.swap_BANG_.call(null,reagent.session.state,(function (p1__35950_SHARP_){
return cljs.core.apply.call(null,cljs.core.partial.call(null,cljs.core.update_in,p1__35950_SHARP_,ks,f),args);
}));
});

reagent.session.update_in_BANG_.cljs$lang$maxFixedArity = (2);

reagent.session.update_in_BANG_.cljs$lang$applyTo = (function (seq35951){
var G__35952 = cljs.core.first.call(null,seq35951);
var seq35951__$1 = cljs.core.next.call(null,seq35951);
var G__35953 = cljs.core.first.call(null,seq35951__$1);
var seq35951__$2 = cljs.core.next.call(null,seq35951__$1);
return reagent.session.update_in_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__35952,G__35953,seq35951__$2);
});

//# sourceMappingURL=session.js.map