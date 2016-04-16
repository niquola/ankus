// Compiled by ClojureScript 1.8.40 {}
goog.provide('route_map.core');
goog.require('cljs.core');
goog.require('clojure.string');
/**
 * build url from route and params
 */
route_map.core.to_url = (function route_map$core$to_url(var_args){
var args__34839__auto__ = [];
var len__34832__auto___35963 = arguments.length;
var i__34833__auto___35964 = (0);
while(true){
if((i__34833__auto___35964 < len__34832__auto___35963)){
args__34839__auto__.push((arguments[i__34833__auto___35964]));

var G__35965 = (i__34833__auto___35964 + (1));
i__34833__auto___35964 = G__35965;
continue;
} else {
}
break;
}

var argseq__34840__auto__ = ((((1) < args__34839__auto__.length))?(new cljs.core.IndexedSeq(args__34839__auto__.slice((1)),(0),null)):null);
return route_map.core.to_url.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__34840__auto__);
});

route_map.core.to_url.cljs$core$IFn$_invoke$arity$variadic = (function (route,p__35961){
var vec__35962 = p__35961;
var params = cljs.core.nth.call(null,vec__35962,(0),null);
return null;
});

route_map.core.to_url.cljs$lang$maxFixedArity = (1);

route_map.core.to_url.cljs$lang$applyTo = (function (seq35959){
var G__35960 = cljs.core.first.call(null,seq35959);
var seq35959__$1 = cljs.core.next.call(null,seq35959);
return route_map.core.to_url.cljs$core$IFn$_invoke$arity$variadic(G__35960,seq35959__$1);
});
route_map.core.pathify = (function route_map$core$pathify(path){
return cljs.core.filterv.call(null,(function (p1__35966_SHARP_){
return !(clojure.string.blank_QMARK_.call(null,p1__35966_SHARP_));
}),clojure.string.split.call(null,path,/\//));
});

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
route_map.core.Match = (function (parents,params,match,__meta,__extmap,__hash){
this.parents = parents;
this.params = params;
this.match = match;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 8192;
})
route_map.core.Match.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__34385__auto__,k__34386__auto__){
var self__ = this;
var this__34385__auto____$1 = this;
return cljs.core._lookup.call(null,this__34385__auto____$1,k__34386__auto__,null);
});

route_map.core.Match.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__34387__auto__,k35968,else__34388__auto__){
var self__ = this;
var this__34387__auto____$1 = this;
var G__35970 = (((k35968 instanceof cljs.core.Keyword))?k35968.fqn:null);
switch (G__35970) {
case "parents":
return self__.parents;

break;
case "params":
return self__.params;

break;
case "match":
return self__.match;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k35968,else__34388__auto__);

}
});

route_map.core.Match.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__34399__auto__,writer__34400__auto__,opts__34401__auto__){
var self__ = this;
var this__34399__auto____$1 = this;
var pr_pair__34402__auto__ = ((function (this__34399__auto____$1){
return (function (keyval__34403__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__34400__auto__,cljs.core.pr_writer,""," ","",opts__34401__auto__,keyval__34403__auto__);
});})(this__34399__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__34400__auto__,pr_pair__34402__auto__,"#route-map.core.Match{",", ","}",opts__34401__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"parents","parents",-2027538891),self__.parents],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"params","params",710516235),self__.params],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"match","match",1220059550),self__.match],null))], null),self__.__extmap));
});

route_map.core.Match.prototype.cljs$core$IIterable$ = true;

route_map.core.Match.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__35967){
var self__ = this;
var G__35967__$1 = this;
return (new cljs.core.RecordIter((0),G__35967__$1,3,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"parents","parents",-2027538891),new cljs.core.Keyword(null,"params","params",710516235),new cljs.core.Keyword(null,"match","match",1220059550)], null),cljs.core._iterator.call(null,self__.__extmap)));
});

route_map.core.Match.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__34383__auto__){
var self__ = this;
var this__34383__auto____$1 = this;
return self__.__meta;
});

route_map.core.Match.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__34379__auto__){
var self__ = this;
var this__34379__auto____$1 = this;
return (new route_map.core.Match(self__.parents,self__.params,self__.match,self__.__meta,self__.__extmap,self__.__hash));
});

route_map.core.Match.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__34389__auto__){
var self__ = this;
var this__34389__auto____$1 = this;
return (3 + cljs.core.count.call(null,self__.__extmap));
});

route_map.core.Match.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__34380__auto__){
var self__ = this;
var this__34380__auto____$1 = this;
var h__34198__auto__ = self__.__hash;
if(!((h__34198__auto__ == null))){
return h__34198__auto__;
} else {
var h__34198__auto____$1 = cljs.core.hash_imap.call(null,this__34380__auto____$1);
self__.__hash = h__34198__auto____$1;

return h__34198__auto____$1;
}
});

route_map.core.Match.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this__34381__auto__,other__34382__auto__){
var self__ = this;
var this__34381__auto____$1 = this;
if(cljs.core.truth_((function (){var and__33751__auto__ = other__34382__auto__;
if(cljs.core.truth_(and__33751__auto__)){
var and__33751__auto____$1 = (this__34381__auto____$1.constructor === other__34382__auto__.constructor);
if(and__33751__auto____$1){
return cljs.core.equiv_map.call(null,this__34381__auto____$1,other__34382__auto__);
} else {
return and__33751__auto____$1;
}
} else {
return and__33751__auto__;
}
})())){
return true;
} else {
return false;
}
});

route_map.core.Match.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__34394__auto__,k__34395__auto__){
var self__ = this;
var this__34394__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"params","params",710516235),null,new cljs.core.Keyword(null,"parents","parents",-2027538891),null,new cljs.core.Keyword(null,"match","match",1220059550),null], null), null),k__34395__auto__)){
return cljs.core.dissoc.call(null,cljs.core.with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__34394__auto____$1),self__.__meta),k__34395__auto__);
} else {
return (new route_map.core.Match(self__.parents,self__.params,self__.match,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__34395__auto__)),null));
}
});

route_map.core.Match.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__34392__auto__,k__34393__auto__,G__35967){
var self__ = this;
var this__34392__auto____$1 = this;
var pred__35971 = cljs.core.keyword_identical_QMARK_;
var expr__35972 = k__34393__auto__;
if(cljs.core.truth_(pred__35971.call(null,new cljs.core.Keyword(null,"parents","parents",-2027538891),expr__35972))){
return (new route_map.core.Match(G__35967,self__.params,self__.match,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__35971.call(null,new cljs.core.Keyword(null,"params","params",710516235),expr__35972))){
return (new route_map.core.Match(self__.parents,G__35967,self__.match,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__35971.call(null,new cljs.core.Keyword(null,"match","match",1220059550),expr__35972))){
return (new route_map.core.Match(self__.parents,self__.params,G__35967,self__.__meta,self__.__extmap,null));
} else {
return (new route_map.core.Match(self__.parents,self__.params,self__.match,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__34393__auto__,G__35967),null));
}
}
}
});

route_map.core.Match.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__34397__auto__){
var self__ = this;
var this__34397__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"parents","parents",-2027538891),self__.parents],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"params","params",710516235),self__.params],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"match","match",1220059550),self__.match],null))], null),self__.__extmap));
});

route_map.core.Match.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__34384__auto__,G__35967){
var self__ = this;
var this__34384__auto____$1 = this;
return (new route_map.core.Match(self__.parents,self__.params,self__.match,G__35967,self__.__extmap,self__.__hash));
});

route_map.core.Match.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__34390__auto__,entry__34391__auto__){
var self__ = this;
var this__34390__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__34391__auto__)){
return cljs.core._assoc.call(null,this__34390__auto____$1,cljs.core._nth.call(null,entry__34391__auto__,(0)),cljs.core._nth.call(null,entry__34391__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__34390__auto____$1,entry__34391__auto__);
}
});

route_map.core.Match.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"parents","parents",-387007364,null),new cljs.core.Symbol(null,"params","params",-1943919534,null),new cljs.core.Symbol(null,"match","match",-1434376219,null)], null);
});

route_map.core.Match.cljs$lang$type = true;

route_map.core.Match.cljs$lang$ctorPrSeq = (function (this__34419__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"route-map.core/Match");
});

route_map.core.Match.cljs$lang$ctorPrWriter = (function (this__34419__auto__,writer__34420__auto__){
return cljs.core._write.call(null,writer__34420__auto__,"route-map.core/Match");
});

route_map.core.__GT_Match = (function route_map$core$__GT_Match(parents,params,match){
return (new route_map.core.Match(parents,params,match,null,null,null));
});

route_map.core.map__GT_Match = (function route_map$core$map__GT_Match(G__35969){
return (new route_map.core.Match(new cljs.core.Keyword(null,"parents","parents",-2027538891).cljs$core$IFn$_invoke$arity$1(G__35969),new cljs.core.Keyword(null,"params","params",710516235).cljs$core$IFn$_invoke$arity$1(G__35969),new cljs.core.Keyword(null,"match","match",1220059550).cljs$core$IFn$_invoke$arity$1(G__35969),null,cljs.core.dissoc.call(null,G__35969,new cljs.core.Keyword(null,"parents","parents",-2027538891),new cljs.core.Keyword(null,"params","params",710516235),new cljs.core.Keyword(null,"match","match",1220059550)),null));
});

route_map.core.is_glob_QMARK_ = (function route_map$core$is_glob_QMARK_(k){
var s = cljs.core.name.call(null,k);
return cljs.core._EQ_.call(null,s.indexOf("*"),(s.length - (1)));
});
route_map.core.get_param = (function route_map$core$get_param(node){
return cljs.core.first.call(null,cljs.core.filter.call(null,(function (p__35977){
var vec__35978 = p__35977;
var k = cljs.core.nth.call(null,vec__35978,(0),null);
var v = cljs.core.nth.call(null,vec__35978,(1),null);
return cljs.core.vector_QMARK_.call(null,k);
}),node));
});
route_map.core._match = (function route_map$core$_match(rs,pth){
var acc = route_map.core.__GT_Match.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.PersistentArrayMap.EMPTY,null);
var G__35986 = pth;
var vec__35987 = G__35986;
var x = cljs.core.nth.call(null,vec__35987,(0),null);
var rpth = cljs.core.nthnext.call(null,vec__35987,(1));
var pth__$1 = vec__35987;
var node = rs;
var acc__$1 = acc;
var G__35986__$1 = G__35986;
var node__$1 = node;
while(true){
var acc__$2 = acc__$1;
var vec__35988 = G__35986__$1;
var x__$1 = cljs.core.nth.call(null,vec__35988,(0),null);
var rpth__$1 = cljs.core.nthnext.call(null,vec__35988,(1));
var pth__$2 = vec__35988;
var node__$2 = node__$1;
if(cljs.core.empty_QMARK_.call(null,pth__$2)){
if(cljs.core.truth_(node__$2)){
if((cljs.core.map_QMARK_.call(null,node__$2)) && (cljs.core.contains_QMARK_.call(null,node__$2,new cljs.core.Keyword(null,".",".",335144435)))){
return cljs.core.assoc.call(null,cljs.core.update_in.call(null,acc__$2,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"parents","parents",-2027538891)], null),cljs.core.conj,node__$2),new cljs.core.Keyword(null,"match","match",1220059550),new cljs.core.Keyword(null,".",".",335144435).cljs$core$IFn$_invoke$arity$1(node__$2));
} else {
return cljs.core.assoc.call(null,acc__$2,new cljs.core.Keyword(null,"match","match",1220059550),node__$2);
}
} else {
return null;
}
} else {
var node__$3 = ((cljs.core.var_QMARK_.call(null,node__$2))?cljs.core.deref.call(null,node__$2):node__$2);
var temp__4655__auto__ = cljs.core.get.call(null,node__$3,x__$1);
if(cljs.core.truth_(temp__4655__auto__)){
var nnode = temp__4655__auto__;
var G__35991 = cljs.core.update_in.call(null,acc__$2,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"parents","parents",-2027538891)], null),cljs.core.conj,node__$3);
var G__35992 = rpth__$1;
var G__35993 = nnode;
acc__$1 = G__35991;
G__35986__$1 = G__35992;
node__$1 = G__35993;
continue;
} else {
var temp__4657__auto__ = (function (){var and__33751__auto__ = !((x__$1 instanceof cljs.core.Keyword));
if(and__33751__auto__){
var and__33751__auto____$1 = cljs.core.map_QMARK_.call(null,node__$3);
if(and__33751__auto____$1){
return route_map.core.get_param.call(null,node__$3);
} else {
return and__33751__auto____$1;
}
} else {
return and__33751__auto__;
}
})();
if(cljs.core.truth_(temp__4657__auto__)){
var vec__35989 = temp__4657__auto__;
var vec__35990 = cljs.core.nth.call(null,vec__35989,(0),null);
var k = cljs.core.nth.call(null,vec__35990,(0),null);
var nnode = cljs.core.nth.call(null,vec__35989,(1),null);
var acc__$3 = cljs.core.update_in.call(null,acc__$2,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"parents","parents",-2027538891)], null),cljs.core.conj,node__$3);
if(cljs.core.truth_(route_map.core.is_glob_QMARK_.call(null,k))){
var G__35994 = cljs.core.update_in.call(null,acc__$3,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"params","params",710516235)], null),cljs.core.assoc,k,cljs.core.into.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.butlast.call(null,pth__$2)));
var G__35995 = new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.last.call(null,pth__$2)], null);
var G__35996 = nnode;
acc__$1 = G__35994;
G__35986__$1 = G__35995;
node__$1 = G__35996;
continue;
} else {
var G__35997 = cljs.core.update_in.call(null,acc__$3,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"params","params",710516235)], null),cljs.core.assoc,k,x__$1);
var G__35998 = rpth__$1;
var G__35999 = nnode;
acc__$1 = G__35997;
G__35986__$1 = G__35998;
node__$1 = G__35999;
continue;
}
} else {
return null;
}
}
}
break;
}
});
route_map.core.match = (function route_map$core$match(path,routes){
if(cljs.core.vector_QMARK_.call(null,path)){
var vec__36001 = path;
var meth = cljs.core.nth.call(null,vec__36001,(0),null);
var url = cljs.core.nth.call(null,vec__36001,(1),null);
return route_map.core._match.call(null,routes,cljs.core.conj.call(null,route_map.core.pathify.call(null,url),cljs.core.keyword.call(null,clojure.string.upper_case.call(null,cljs.core.name.call(null,meth)))));
} else {
return route_map.core._match.call(null,routes,route_map.core.pathify.call(null,path));
}
});
route_map.core.wrap_route_map = (function route_map$core$wrap_route_map(h,routes){

return (function (p__36005){
var map__36006 = p__36005;
var map__36006__$1 = ((((!((map__36006 == null)))?((((map__36006.cljs$lang$protocol_mask$partition0$ & (64))) || (map__36006.cljs$core$ISeq$))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36006):map__36006);
var req = map__36006__$1;
var meth = cljs.core.get.call(null,map__36006__$1,new cljs.core.Keyword(null,"request-method","request-method",1764796830));
var uri = cljs.core.get.call(null,map__36006__$1,new cljs.core.Keyword(null,"uri","uri",-774711847));
var temp__4655__auto__ = route_map.core.match.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [meth,uri], null),routes);
if(cljs.core.truth_(temp__4655__auto__)){
var match = temp__4655__auto__;
return h.call(null,cljs.core.assoc.call(null,req,new cljs.core.Keyword(null,"route-match","route-match",-1450985937),match));
} else {
return h.call(null,req);
}
});
});

//# sourceMappingURL=core.js.map