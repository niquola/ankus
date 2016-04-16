// Compiled by ClojureScript 1.8.40 {}
goog.provide('cljs.repl');
goog.require('cljs.core');
cljs.repl.print_doc = (function cljs$repl$print_doc(m){
cljs.core.println.call(null,"-------------------------");

cljs.core.println.call(null,[cljs.core.str((function (){var temp__4657__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__4657__auto__)){
var ns = temp__4657__auto__;
return [cljs.core.str(ns),cljs.core.str("/")].join('');
} else {
return null;
}
})()),cljs.core.str(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Protocol");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__34932_34946 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__34933_34947 = null;
var count__34934_34948 = (0);
var i__34935_34949 = (0);
while(true){
if((i__34935_34949 < count__34934_34948)){
var f_34950 = cljs.core._nth.call(null,chunk__34933_34947,i__34935_34949);
cljs.core.println.call(null,"  ",f_34950);

var G__34951 = seq__34932_34946;
var G__34952 = chunk__34933_34947;
var G__34953 = count__34934_34948;
var G__34954 = (i__34935_34949 + (1));
seq__34932_34946 = G__34951;
chunk__34933_34947 = G__34952;
count__34934_34948 = G__34953;
i__34935_34949 = G__34954;
continue;
} else {
var temp__4657__auto___34955 = cljs.core.seq.call(null,seq__34932_34946);
if(temp__4657__auto___34955){
var seq__34932_34956__$1 = temp__4657__auto___34955;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__34932_34956__$1)){
var c__34574__auto___34957 = cljs.core.chunk_first.call(null,seq__34932_34956__$1);
var G__34958 = cljs.core.chunk_rest.call(null,seq__34932_34956__$1);
var G__34959 = c__34574__auto___34957;
var G__34960 = cljs.core.count.call(null,c__34574__auto___34957);
var G__34961 = (0);
seq__34932_34946 = G__34958;
chunk__34933_34947 = G__34959;
count__34934_34948 = G__34960;
i__34935_34949 = G__34961;
continue;
} else {
var f_34962 = cljs.core.first.call(null,seq__34932_34956__$1);
cljs.core.println.call(null,"  ",f_34962);

var G__34963 = cljs.core.next.call(null,seq__34932_34956__$1);
var G__34964 = null;
var G__34965 = (0);
var G__34966 = (0);
seq__34932_34946 = G__34963;
chunk__34933_34947 = G__34964;
count__34934_34948 = G__34965;
i__34935_34949 = G__34966;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_34967 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__33763__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__33763__auto__)){
return or__33763__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.call(null,arglists_34967);
} else {
cljs.core.prn.call(null,((cljs.core._EQ_.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first.call(null,arglists_34967)))?cljs.core.second.call(null,arglists_34967):arglists_34967));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Special Form");

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.contains_QMARK_.call(null,m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.call(null,[cljs.core.str("\n  Please see http://clojure.org/"),cljs.core.str(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))].join(''));
} else {
return null;
}
} else {
return cljs.core.println.call(null,[cljs.core.str("\n  Please see http://clojure.org/special_forms#"),cljs.core.str(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Macro");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"REPL Special Function");
} else {
}

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__34936 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__34937 = null;
var count__34938 = (0);
var i__34939 = (0);
while(true){
if((i__34939 < count__34938)){
var vec__34940 = cljs.core._nth.call(null,chunk__34937,i__34939);
var name = cljs.core.nth.call(null,vec__34940,(0),null);
var map__34941 = cljs.core.nth.call(null,vec__34940,(1),null);
var map__34941__$1 = ((((!((map__34941 == null)))?((((map__34941.cljs$lang$protocol_mask$partition0$ & (64))) || (map__34941.cljs$core$ISeq$))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__34941):map__34941);
var doc = cljs.core.get.call(null,map__34941__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists = cljs.core.get.call(null,map__34941__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name);

cljs.core.println.call(null," ",arglists);

if(cljs.core.truth_(doc)){
cljs.core.println.call(null," ",doc);
} else {
}

var G__34968 = seq__34936;
var G__34969 = chunk__34937;
var G__34970 = count__34938;
var G__34971 = (i__34939 + (1));
seq__34936 = G__34968;
chunk__34937 = G__34969;
count__34938 = G__34970;
i__34939 = G__34971;
continue;
} else {
var temp__4657__auto__ = cljs.core.seq.call(null,seq__34936);
if(temp__4657__auto__){
var seq__34936__$1 = temp__4657__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__34936__$1)){
var c__34574__auto__ = cljs.core.chunk_first.call(null,seq__34936__$1);
var G__34972 = cljs.core.chunk_rest.call(null,seq__34936__$1);
var G__34973 = c__34574__auto__;
var G__34974 = cljs.core.count.call(null,c__34574__auto__);
var G__34975 = (0);
seq__34936 = G__34972;
chunk__34937 = G__34973;
count__34938 = G__34974;
i__34939 = G__34975;
continue;
} else {
var vec__34943 = cljs.core.first.call(null,seq__34936__$1);
var name = cljs.core.nth.call(null,vec__34943,(0),null);
var map__34944 = cljs.core.nth.call(null,vec__34943,(1),null);
var map__34944__$1 = ((((!((map__34944 == null)))?((((map__34944.cljs$lang$protocol_mask$partition0$ & (64))) || (map__34944.cljs$core$ISeq$))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__34944):map__34944);
var doc = cljs.core.get.call(null,map__34944__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists = cljs.core.get.call(null,map__34944__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name);

cljs.core.println.call(null," ",arglists);

if(cljs.core.truth_(doc)){
cljs.core.println.call(null," ",doc);
} else {
}

var G__34976 = cljs.core.next.call(null,seq__34936__$1);
var G__34977 = null;
var G__34978 = (0);
var G__34979 = (0);
seq__34936 = G__34976;
chunk__34937 = G__34977;
count__34938 = G__34978;
i__34939 = G__34979;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
}
});

//# sourceMappingURL=repl.js.map