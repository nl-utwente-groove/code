<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="bipartite">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>40 40 40 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>160 40 40 30</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>280 40 40 30</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>400 40 40 30</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>100 200 40 30</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>300 200 40 30</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge from="n0" to="n4">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n0" to="n5">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n1" to="n4">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n1" to="n5">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n2" to="n4">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n2" to="n5">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n3" to="n4">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n3" to="n5">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
    </graph>
</gxl>
