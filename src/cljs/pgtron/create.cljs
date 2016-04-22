(ns pgtron.create
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [chloroform.core :as form]
            [charty.core :as chart]
            [pgtron.style :refer [style icon]]))

(def docs
  [:div.docs
   [:h1 "CREATE DATABASE"]
   [:pre [:code "CREATE DATABASE name
      [ [ WITH ] [ OWNER [=] user_name ]
             [ TEMPLATE [=] template ]
             [ ENCODING [=] encoding ]
             [ LC_COLLATE [=] lc_collate ]
             [ LC_CTYPE [=] lc_ctype ]
             [ TABLESPACE [=] tablespace ]
             [ CONNECTION LIMIT [=] connlimit ] ]"]]

   
  [:h2 "Description"]

  [:p "CREATE DATABASE creates a new PostgreSQL database."]

  [:p "To create a database, you must be a superuser or have the special CREATEDB
  privilege. See CREATE USER."]

  [:p "
  By default, the new database will be created by cloning the standard system
  database template1. A different template can be specified by writing TEMPLATE
  name. In particular, by writing TEMPLATE template0, you can create a virgin
  database containing only the standard objects predefined by your version of
  PostgreSQL. This is useful if you wish to avoid copying any installation-local
  objects that might have been added to template1."]

   [:h2  "Parameters"]

   [:dl
    [:dt "name"]
    [:dd "The name of a database to create."]

    [:dt "user_name"]
    [:dd "The role name of the user who will own the new database, or DEFAULT
          to use the default (namely, the user executing the command). To create a
          database owned by another role, you must be a direct or indirect member of
          that role, or be a superuser."]

    ]

  [:pre "
  template

  The name of the template from which to create the new database, or
  DEFAULT to use the default template (template1).

  encoding

  Character set encoding to use in the new database. Specify a string
  constant (e.g., 'SQL_ASCII'), or an integer encoding number, or DEFAULT to use
  the default encoding (namely, the encoding of the template database). The
  character sets supported by the PostgreSQL server are described in Section
  22.3.1. See below for additional restrictions.

  lc_collate

  Collation order (LC_COLLATE) to use in the new database. This affects the sort
  order applied to strings, e.g. in queries with ORDER BY, as well as the order
  used in indexes on text columns. The default is to use the collation order of
  the template database. See below for additional restrictions.

  lc_ctype

  Character classification (LC_CTYPE) to use in the new database. This affects
  the categorization of characters, e.g. lower, upper and digit. The default is
  to use the character classification of the template database. See below for
  additional restrictions.

  tablespace

  The name of the tablespace that will be associated with the new database, or
  DEFAULT to use the template database's tablespace. This tablespace will be the
  default tablespace used for objects created in this database. See CREATE
  TABLESPACE for more information.

  connlimit

  How many concurrent connections can be made to this database. -1 (the default)
  means no limit.

  Optional parameters can be written in any order, not only the order
  illustrated above.

  Notes

  CREATE DATABASE cannot be executed inside a transaction block.
  "]

  [:p "Errors along the line of \"could not initialize database directory\" are most
  likely related to insufficient permissions on the data directory, a full disk,
  or other file system problems."]

  [:p "Use DROP DATABASE to remove a database. "]

  [:p "The program createdb is a wrapper program around this command, provided for convenience. "]

  [:p "Although it is possible to copy a database other than template1 by specifying
  its name as the template, this is not (yet) intended as a
  general-purpose \"COPY DATABASE\" facility. The principal limitation is that no
  other sessions can be connected to the template database while it is being
  copied. CREATE DATABASE will fail if any other connection exists when it
  starts; otherwise, new connections to the template database are locked out
  until CREATE DATABASE completes. See Section 21.3 for more information. "]

  [:p "The character set encoding specified for the new database must be compatible
with the chosen locale settings (LC_COLLATE and LC_CTYPE). If the locale is
C (or equivalently POSIX), then all encodings are allowed, but for other locale
settings there is only one encoding that will work properly. (On Windows,
however, UTF-8 encoding can be used with any locale.) CREATE DATABASE will allow
superusers to specify SQL_ASCII encoding regardless of the locale settings, but
this choice is deprecated and may result in misbehavior of character-string
functions if data that is not encoding-compatible with the locale is stored in
the database."]

  [:p "The encoding and locale settings must match those of the template database,
except when template0 is used as template. This is because other databases might
contain data that does not match the specified encoding, or might contain
indexes whose sort ordering is affected by LC_COLLATE and LC_CTYPE. Copying such
data would result in a database that is corrupt according to the new settings.
template0, however, is known to not contain any data or indexes that would be
affected."]

  [:p "
The CONNECTION LIMIT option is only enforced approximately; if two new sessions
start at about the same time when just one connection \"slot\" remains for the
database, it is possible that both will fail. Also, the limit is not enforced
against superusers."]

  [:p "
Examples

To create a new database:

CREATE DATABASE lusiadas;

To create a database sales owned by user salesapp with a default tablespace of
salesspace:

CREATE DATABASE sales OWNER salesapp TABLESPACE salesspace;

To create a database music which supports the ISO-8859-1 character set:

CREATE DATABASE music ENCODING 'LATIN1' TEMPLATE template0;

In this example, the TEMPLATE template0 clause would only be required if
template1's encoding is not ISO-8859-1. Note that changing encoding might
require selecting new LC_COLLATE and LC_CTYPE settings as well.

Compatibility

There is no CREATE DATABASE statement in the SQL standard. Databases are
equivalent to catalogs, whose creation is implementation-defined.
  "]])

(def create-statement "
   CREATE DATABASE <name>
              WITH
             OWNER = <user_name>
          TEMPLATE = <template>
          ENCODING = <encoding>
        LC_COLLATE = <lc_collate>
          LC_CTYPE = <lc_ctype>
        TABLESPACE = <tablespace>
  CONNECTION LIMIT = <connlimit>
  ")

(defn $index [params]
  (let [state (atom {:sql create-statement})
        handle (fn [] (println "hi"))]
    (fn []
      [l/layout {:bread-crump [{:title "Create Database"}]}
       [:div#new
        (style [:#new {:display "flex"
                       :height "100%"
                       :outline "1px solid gray"
                       :$color :gray
                       :flex-direction "row"}
                [:h]
                [:.card {:$padding [1 2]}]
                [:#main {:flex-grow 1
                         :flex-basis 0;
                         :outline "1px solid #666"}]
                [:#docs {:flex-grow 1
                         :flex-basis 0;
                         :border-top "1px solid #666"
                         :$color [:black :white]
                         :$padding [1 2]
                         :overflow-y "scroll"}]])
        [:div#main
         [form/codemirror state [:sql] {:theme "railscasts"
                                        :mode "text/x-sql"
                                        :extraKeys {"Ctrl-Enter" handle}}]]
        [:div#docs docs]]])))

(def routes {"database" {:GET #'$index}})
