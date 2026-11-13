<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="polyConst-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>80 80 60 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>80 190 60 30</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>80 300 60 30</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>80 410 60 30</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:x=UserTypePoly("p",1,2,3)</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>let:x=UserTypePoly("p",1,2)</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>let:x=UserTypePoly("p")</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>let:x=UserTypePoly("q",1,2,3)</string>
            </attr>
        </edge>
    </graph>
</gxl>
