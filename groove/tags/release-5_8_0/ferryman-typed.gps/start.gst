<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n147">
            <attr name="layout">
                <string>405 122 30 30</string>
            </attr>
        </node>
        <node id="n146">
            <attr name="layout">
                <string>67 127 29 15</string>
            </attr>
        </node>
        <node id="n148">
            <attr name="layout">
                <string>67 36 31 15</string>
            </attr>
        </node>
        <node id="n149">
            <attr name="layout">
                <string>287 46 29 15</string>
            </attr>
        </node>
        <node id="n150">
            <attr name="layout">
                <string>172 123 30 30</string>
            </attr>
        </node>
        <node id="n151">
            <attr name="layout">
                <string>59 232 53 15</string>
            </attr>
        </node>
        <edge from="n147" to="n147">
            <attr name="label">
                <string>type:Bank</string>
            </attr>
        </edge>
        <edge from="n147" to="n147">
            <attr name="label">
                <string>flag:right</string>
            </attr>
        </edge>
        <edge from="n146" to="n146">
            <attr name="label">
                <string>type:Goat</string>
            </attr>
        </edge>
        <edge from="n146" to="n151">
            <attr name="label">
                <string>likes</string>
            </attr>
        </edge>
        <edge from="n146" to="n150">
            <attr name="label">
                <string>on</string>
            </attr>
        </edge>
        <edge from="n148" to="n148">
            <attr name="label">
                <string>type:Wolf</string>
            </attr>
        </edge>
        <edge from="n148" to="n146">
            <attr name="label">
                <string>likes</string>
            </attr>
        </edge>
        <edge from="n148" to="n150">
            <attr name="label">
                <string>on</string>
            </attr>
        </edge>
        <edge from="n149" to="n149">
            <attr name="label">
                <string>type:Boat</string>
            </attr>
        </edge>
        <edge from="n149" to="n150">
            <attr name="label">
                <string>moored</string>
            </attr>
        </edge>
        <edge from="n150" to="n150">
            <attr name="label">
                <string>type:Bank</string>
            </attr>
        </edge>
        <edge from="n150" to="n150">
            <attr name="label">
                <string>flag:left</string>
            </attr>
        </edge>
        <edge from="n151" to="n151">
            <attr name="label">
                <string>type:Cabbage</string>
            </attr>
        </edge>
        <edge from="n151" to="n150">
            <attr name="label">
                <string>on</string>
            </attr>
        </edge>
    </graph>
</gxl>
