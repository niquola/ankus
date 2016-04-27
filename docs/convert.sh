for f in **/*.sgml; do
    echo "$f > ${f%.sgml}.xml"
    sgml2xml -x lower "$f" > "${f%.sgml}.xml"
done
